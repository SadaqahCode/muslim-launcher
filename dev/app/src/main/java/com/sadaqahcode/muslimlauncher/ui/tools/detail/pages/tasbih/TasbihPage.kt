package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.domain.model.tools.HapticMode
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.Wallpaper
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components.TasbihContent

class Actions(
    val onTap: () -> Unit,
    val onReset: () -> Unit,
    val onSelectFree: (String) -> Unit,
    val onMoveFree: (Int) -> Unit,
    val onToggleFavFree: (String) -> Unit,
    val onToggleFavGuided: (String) -> Unit,
    val onSelectTarget: (Int) -> Unit,
    val onStartGuided: (Int) -> Unit,
    val onRestartGuided: () -> Unit,
    val onExitGuided: () -> Unit,
    val onNext: () -> Unit,
    val onPrevious: () -> Unit,
    val onRetry: () -> Unit,
    val onSetHapticMode: (HapticMode) -> Unit,
)

@Composable
internal fun TasbihPage(
    tool: Tool,
    onClose: () -> Unit,
    topTransparentModal: Dp,
    viewModel: TasbihViewModel = hiltViewModel(),
) {
    val lc = LocalLegibleColors.current
    val colors = lc.scheme
    val userSettings by viewModel.settings.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val language = LocalLocale.current.platformLocale.language
    val vibrator = remember(context) { context.vibratorOrNull() }

    LaunchedEffect(viewModel, vibrator) {
        viewModel.haptics.collect { event ->
            when (viewModel.state.value.hapticMode) {
                HapticMode.Haptic -> haptic.performHapticFeedback(event.hapticFeedbackType())
                HapticMode.Vibration -> vibrator?.vibrateCompat(event)
                HapticMode.Silent -> Unit
            }
        }
    }

    val actions = remember(viewModel) {
        Actions(
            onTap = viewModel::tap,
            onReset = viewModel::requestReset,
            onSelectFree = viewModel::selectFree,
            onMoveFree = viewModel::moveFree,
            onToggleFavFree = viewModel::toggleFavoriteFree,
            onToggleFavGuided = viewModel::toggleFavoriteGuided,
            onSelectTarget = viewModel::selectTarget,
            onStartGuided = viewModel::startGuided,
            onRestartGuided = viewModel::restartGuided,
            onExitGuided = viewModel::exitGuided,
            onNext = viewModel::nextEntry,
            onPrevious = viewModel::previousEntry,
            onRetry = viewModel::retry,
            onSetHapticMode = viewModel::setHapticMode,
        )
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Box(Modifier.fillMaxSize()) {
        Wallpaper(settings = userSettings, modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { _ ->
            Box(Modifier.fillMaxSize()) {
                TasbihContent(
                    toolTitle = stringResource(tool.titleRes),
                    state = state,
                    language = language,
                    actions = actions,
                    onClose = onClose,
                    scheme = colors,
                    topTransparentModal = topTransparentModal,
                )

                // Overlay — no reserved space, no layout shift when it shows/hides.
                if (state.isRefreshing) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .height(2.dp),
                        color = colors.primary,
                        trackColor = colors.primary.copy(alpha = 0.15f),
                    )
                }
            }
        }
    }
}

// ── Dispatcher helpers ─────────────────────────────────────────

private fun TasbihHaptic.hapticFeedbackType(): HapticFeedbackType = when (this) {
    TasbihHaptic.Tap, TasbihHaptic.ResetConfirmNeeded -> HapticFeedbackType.TextHandleMove
    TasbihHaptic.TargetReached, TasbihHaptic.ResetConfirmed,
    TasbihHaptic.StepDone, TasbihHaptic.RoutineFinished -> HapticFeedbackType.LongPress
}

private fun Context.vibratorOrNull(): Vibrator? = try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
} catch (_: Exception) { null }

private fun Vibrator.vibrateCompat(event: TasbihHaptic) {
    val ms: Long = when (event) {
        TasbihHaptic.Tap -> 12L
        TasbihHaptic.ResetConfirmNeeded -> 20L
        TasbihHaptic.TargetReached, TasbihHaptic.ResetConfirmed,
        TasbihHaptic.StepDone, TasbihHaptic.RoutineFinished -> 35L
    }
    val amplitude: Int = when (event) {
        TasbihHaptic.Tap -> 80
        TasbihHaptic.ResetConfirmNeeded -> 130
        else -> 200
    }
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrate(VibrationEffect.createOneShot(ms, amplitude))
        } else {
            @Suppress("DEPRECATION")
            vibrate(ms)
        }
    } catch (_: Exception) { /* DND / ringer silent may reject; fine */ }
}