package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.ui.common.layout.PageMaxWidthLargeScreen
import com.sadaqahcode.muslimlauncher.ui.common.layout.PageMaxWidthScreen
import com.sadaqahcode.muslimlauncher.ui.common.layout.ScreenLayout
import com.sadaqahcode.muslimlauncher.ui.components.AdaptiveSidebarLayout
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.Wallpaper
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components.CalibrationDialog
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components.HeaderQibla
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components.InfoPanel
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components.dialog.PermissionStatusDialog
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components.QiblaCompass
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components.QiblaGuideContent

private const val PERMISSION_COARSE = Manifest.permission.ACCESS_COARSE_LOCATION
private const val PERMISSION_FINE = Manifest.permission.ACCESS_FINE_LOCATION

@Composable
internal fun QiblaPage(
    tool: Tool,
    onClose: () -> Unit,
    screen: ScreenLayout,
    viewModel: QiblaViewModel = hiltViewModel(),
) {
    val lc = LocalLegibleColors.current
    val lsColors = lc.scheme
    val defaultColors = MaterialTheme.colorScheme

    val userSettings by viewModel.settings.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val language = LocalLocale.current.platformLocale.language
    var showGuide by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.haptics.collect { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.onResume() }
    LifecycleResumeEffect(viewModel) {
        viewModel.startHeading()
        onPauseOrDispose { viewModel.stopHeading() }
    }

    // ── Permission launchers ─────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.onPermissionGranted()
        } else {
            val activity = context.findActivity()
            val blocked = activity != null &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION_COARSE)
            viewModel.onPermissionDenied(blocked)
        }
    }

    // Separate launcher for FINE. Android 12+ may show the "Change to precise"
    // upgrade prompt; older versions grant silently if the user allows.
    val precisionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPrecisionResult(granted)
    }

    val requestPermission: () -> Unit = {
        viewModel.dismissDialog()
        permissionLauncher.launch(PERMISSION_COARSE)
    }

    val requestPrecise: () -> Unit = {
        viewModel.dismissDialog()
        precisionLauncher.launch(PERMISSION_FINE)
    }

    val openAppSettings: () -> Unit = {
        viewModel.onOpenAppSettings()
        context.openSettings(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null),
            )
        )
    }

    val openLocationSettings: () -> Unit = {
        viewModel.onOpenLocationSettings()
        context.openSettings(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    val labels = compassLabels(language)

    val compass: @Composable (Modifier) -> Unit = { m ->
        BoxWithConstraints(m, contentAlignment = Alignment.Center) {
            val side = (minOf(maxWidth, maxHeight, 380.dp) - 16.dp).coerceAtLeast(160.dp)
            val bearing = state.qiblaBearing
            QiblaCompass(
                dialRotation = state.dialRotation,
                qiblaBearing = bearing,
                aligned = state.aligned,
                labels = labels,
                centerPrimary = bearing?.let { "${it.toInt()}°" } ?: "—",
                centerSecondary = bearing?.let { compassName(language, it) },
                description = tr(language, "Kompas arah kiblat", "Qibla compass"),
                stateText = statusText(state, language),
                lsScheme = lsColors,
                defaultScheme = defaultColors,
                modifier = Modifier.size(side),
            )
        }
    }

    val info: @Composable (Modifier) -> Unit = { m ->
        InfoPanel(
            state = state,
            language = language,
            onRequestPermission = requestPermission,
            onOpenSettings = openAppSettings,
            onRefresh = viewModel::onRefreshClick,
            onEdit = {
                if (state.precision == LocationPrecision.Coarse) {
                    precisionLauncher.launch(PERMISSION_FINE)
                } else {
                    viewModel.onRefreshClick()
                }
            },
            onShowCalibration = viewModel::showCalibrationGuide,
            lsScheme = lsColors,
            defaultScheme = defaultColors,
            modifier = m,
        )
    }

    val permanentSidebar = screen.isLargeLandscape
    val contentMaxWidth = if (permanentSidebar) PageMaxWidthLargeScreen else PageMaxWidthScreen

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Wallpaper(settings = userSettings, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .widthIn(max = contentMaxWidth)
                .fillMaxSize()
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                contentColor = lsColors.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
            ) { _ ->
                AdaptiveSidebarLayout(
                    isTablet = screen.isTablet,
                    permanentSidebar = permanentSidebar,
                    drawerOpen = showGuide,
                    onDrawerClose = { showGuide = false },
                    onDrawerOpen  = { showGuide = true },
                    sidebarWidth = 320.dp,
                    sidebarWindowInsets = WindowInsets.systemBars,
                    contentWindowInsets = WindowInsets(0),
                    sidebarContent = {
                        QiblaGuideContent(
                            language = language,
                            onClose = if (!permanentSidebar) ({ showGuide = false }) else null,
                            defaultScheme = defaultColors,
                            modifier = Modifier.fillMaxSize(),
                        )
                    },
                    content = {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .safeDrawingPadding(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            if (screen.isSplit) {
                                Column(Modifier.fillMaxSize()) {
                                    HeaderQibla(
                                        tool = tool,
                                        language = language,
                                        onClose = onClose,
                                        onOpenGuide = if (!permanentSidebar) ({ showGuide = true }) else null,
                                        onOpenPermissionStatus = (viewModel::showPermissionStatus)
                                    )
                                    Row(Modifier.weight(1f).fillMaxWidth()) {
                                        compass(Modifier.weight(1f).fillMaxSize())
                                        info(
                                            Modifier
                                                .weight(1f)
                                                .fillMaxSize()
                                                .verticalScroll(rememberScrollState())
                                        )
                                    }
                                }
                            } else {
                                Column(Modifier.fillMaxSize()) {
                                    HeaderQibla(
                                        tool = tool,
                                        language = language,
                                        onClose = onClose,
                                        onOpenGuide = if (!permanentSidebar) ({ showGuide = true }) else null,
                                        onOpenPermissionStatus = (viewModel::showPermissionStatus)
                                    )
                                    compass(Modifier.weight(1f).fillMaxWidth())
                                    info(Modifier.fillMaxWidth())
                                }
                            }
                        }
                    },
                )
            }
        }
    }

    when (state.dialog) {
        QiblaDialog.Permission,
        QiblaDialog.LocationOff,
        QiblaDialog.PrecisionUpgrade,
        QiblaDialog.PermissionStatus -> PermissionStatusDialog(
            language               = language,
            gpsEnabled             = state.gpsEnabled,
            permission             = state.permission,
            precision              = state.precision,
            locationProblem        = state.locationProblem,
            onRequestCoarse        = requestPermission,
            onRequestFine          = requestPrecise,
            onOpenLocationSettings = openLocationSettings,
            onOpenAppSettings      = openAppSettings,
            onRetry                = {
                viewModel.dismissDialog()
                viewModel.onRefreshClick()
            },
            onDismiss              = viewModel::dismissDialog,
            defaultScheme          = defaultColors,
        )

        QiblaDialog.Calibration -> CalibrationDialog(
            language      = language,
            defaultScheme = defaultColors,
            sensor        = state.sensor,
            onDismiss     = viewModel::dismissDialog,
        )

        QiblaDialog.None -> Unit
    }
}

private fun Context.openSettings(intent: Intent) {
    runCatching { startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}