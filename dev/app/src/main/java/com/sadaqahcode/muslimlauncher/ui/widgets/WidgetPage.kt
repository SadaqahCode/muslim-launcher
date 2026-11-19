package com.sadaqahcode.muslimlauncher.ui.widgets

import android.app.Activity
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.content.pm.ResolveInfo
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flagodna.debugprint.debugPrint
import com.sadaqahcode.muslimlauncher.ui.home.helper.doubleTapGesture
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.widgets.components.AddWidgetRow
import com.sadaqahcode.muslimlauncher.ui.widgets.components.EmptyWidgetState
import com.sadaqahcode.muslimlauncher.ui.widgets.components.InlineShortcutActionBar
import com.sadaqahcode.muslimlauncher.ui.widgets.components.ReorderWidgetList
import com.sadaqahcode.muslimlauncher.ui.widgets.components.ShortcutTile
import com.sadaqahcode.muslimlauncher.ui.widgets.components.WidgetPickerSheet
import com.sadaqahcode.muslimlauncher.ui.widgets.components.WidgetRow
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

private val PageMaxWidth = 600.dp
private const val TAG = "WidgetPage"

@Composable
fun WidgetPage(
    viewModel: WidgetPageViewModel,
    pageActive: Boolean,
    onDoubleTapHome: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val lc = LocalLegibleColors.current

    val placements by viewModel.placements.collectAsStateWithLifecycle()
    val shortcuts by viewModel.shortcuts.collectAsStateWithLifecycle()
    val userSettings by viewModel.settings.collectAsStateWithLifecycle()
    val pickerOpen by viewModel.pickerOpen.collectAsStateWithLifecycle()
    val pickerLoading by viewModel.pickerLoading.collectAsStateWithLifecycle()
    val availableWidgets by viewModel.availableProviders.collectAsStateWithLifecycle()
    val availableShortcuts by viewModel.availableShortcuts.collectAsStateWithLifecycle()

    val pendingId = remember { PendingWidgetId() }
    var pendingShortcutSource by remember { mutableStateOf<ResolveInfo?>(null) }
    var isReorderMode by remember { mutableStateOf(false) }
    var editingWidgetId by remember { mutableStateOf<Int?>(null) }
    var editingShortcutId by remember { mutableStateOf<String?>(null) }
    var isAdding by remember { mutableStateOf(false) }
    val hostViewCache = remember { mutableMapOf<Int, AppWidgetHostView>() }
    val emptyInteractionSource = remember { MutableInteractionSource() }
    val blockerInteractionSource = remember { MutableInteractionSource() }
    val listState = rememberLazyListState()
    val textScale = userSettings.textSize

    // Draft hoisted from ReorderWidgetList, so swipe / back can save it
    var latestDraftWidgetIds by remember { mutableStateOf<List<Int>?>(null) }
    var latestDraftShortcutIds by remember { mutableStateOf<List<String>?>(null) }
    var latestRemovedWidgetIds by remember { mutableStateOf<List<Int>>(emptyList()) }
    var latestRemovedShortcutIds by remember { mutableStateOf<List<String>>(emptyList()) }

    fun saveAndClose() {
        val widgetIds = latestDraftWidgetIds ?: placements.map { item -> item.widgetId }
        val shortcutIds = latestDraftShortcutIds ?: shortcuts.map { item -> item.id }
        val removedWidgets = latestRemovedWidgetIds
        val removedShortcuts = latestRemovedShortcutIds

        removedWidgets.forEach { id ->
            viewModel.removeWidget(id)
            hostViewCache.remove(id)
        }
        removedShortcuts.forEach { id ->
            viewModel.removeShortcut(id)
        }
        viewModel.reorderWidgets(widgetIds)
        viewModel.reorderShortcuts(shortcutIds)
        isReorderMode = false
    }

    fun enterReorder() {
        latestDraftWidgetIds = placements.map { item -> item.widgetId }
        latestDraftShortcutIds = shortcuts.map { item -> item.id }
        latestRemovedWidgetIds = emptyList()
        latestRemovedShortcutIds = emptyList()
        isReorderMode = true
    }

    val softScrollbarSettings = ScrollbarSettings.Default.copy(
        thumbThickness = 5.dp,
        thumbUnselectedColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f),
        thumbSelectedColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
        scrollbarPadding = 0.dp,
    )

    LaunchedEffect(pageActive) {
        if (!pageActive) {
            if (isReorderMode) saveAndClose()
            editingWidgetId = null
            editingShortcutId = null
        }
    }

    BackHandler(enabled = editingWidgetId != null) {
        editingWidgetId = null
    }
    BackHandler(enabled = editingShortcutId != null) {
        editingShortcutId = null
    }
    BackHandler(enabled = isReorderMode) {
        saveAndClose()
    }

    // ── Config activity result handler ─────────────────────────────────
    //
    // Runs AFTER the widget's configure activity returns. At this point
    // pendingId.value and pendingId.providerInfo MUST still be populated —
    // they are the only link back to the widget that was just bound.
    // Clearing pendingId before proceedAfterBind() (the previous bug) wiped
    // that link and the handler silently no-op'd.
    val configLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val id = pendingId.value
        val info = pendingId.providerInfo
        debugPrint.d(
            TAG,
            "[WidgetFlow] configLauncher resultCode=${result.resultCode} " +
                    "id=$id info=${info?.provider?.flattenToString()}"
        )
        pendingId.clear()
        if (result.resultCode == Activity.RESULT_OK && id != null && info != null) {
            viewModel.completeAdd(id, info)
        } else if (id != null) {
            viewModel.cancelAdd(id)
        }
        isAdding = false
    }

    // ── Bind permission result handler ─────────────────────────────────
    //
    // The system "Allow widget" dialog. On success, the widget is now bound
    // but pendingId must survive into proceedAfterBind() so the config
    // activity (if any) knows which widget to configure.
    val bindPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val id = pendingId.value
        val info = pendingId.providerInfo
        debugPrint.d(
            TAG,
            "[WidgetFlow] bindPermission resultCode=${result.resultCode} " +
                    "id=$id info=${info?.provider?.flattenToString()}"
        )
        if (result.resultCode == Activity.RESULT_OK && id != null && info != null) {
            proceedAfterBind(
                activity = activity,
                widgetId = id,
                info = info,
                configLauncher = configLauncher,
                onComplete = {
                    // Clear only AFTER the full flow finishes, so any later
                    // callback (config) sees the populated pendingId.
                    pendingId.clear()
                    viewModel.completeAdd(id, info)
                    isAdding = false
                },
                onCancel = {
                    pendingId.clear()
                    viewModel.cancelAdd(id)
                    isAdding = false
                },
            )
        } else {
            if (id != null) viewModel.cancelAdd(id)
            pendingId.clear()
            isAdding = false
        }
    }

    val shortcutLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val source = pendingShortcutSource
        pendingShortcutSource = null
        if (result.resultCode == Activity.RESULT_OK && source != null) {
            val data = result.data ?: return@rememberLauncherForActivityResult
            val extracted = extractShortcutResult(data)
                ?: return@rememberLauncherForActivityResult
            viewModel.completeAddShortcut(
                source = source,
                shortcutIntent = extracted.first,
                shortcutLabel = extracted.second,
            )
        }
    }

    if (isReorderMode) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = PageMaxWidth)
                    .fillMaxSize(),
            ) {
                ReorderWidgetList(
                    textScale = textScale,
                    placements = placements,
                    shortcuts = shortcuts,
                    viewModel = viewModel,
                    hostViewCache = hostViewCache,
                    onDraftChanged = { widgetIds, shortcutIds, removedWidgetIds, removedShortcutIds ->
                        latestDraftWidgetIds = widgetIds
                        latestDraftShortcutIds = shortcutIds
                        latestRemovedWidgetIds = removedWidgetIds
                        latestRemovedShortcutIds = removedShortcutIds
                    },
                    onRemoved = { _, _ -> },
                    onDone = { _, _ ->
                        saveAndClose()
                    },
                )
            }
        }
        return
    }

    Box(
        modifier = Modifier.fillMaxSize().doubleTapGesture(
            onDoubleTap = onDoubleTapHome,
            isGatingOk = {
                editingWidgetId == null &&
                        editingShortcutId == null &&
                        !isReorderMode &&
                        !pickerOpen &&
                        !isAdding &&
                        pageActive
            }
        ),
        contentAlignment = Alignment.TopCenter,
    ){
        Column(
            modifier = Modifier
                .widthIn(max = PageMaxWidth)
                .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        start = 28.dp,
                        end = 4.dp,
                        top = if (isLandscape) 4.dp else 16.dp,
                        bottom = if (isLandscape) 8.dp else 16.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Widgets",
                        style = textScale.scaled(MaterialTheme.typography.titleLarge),
                        fontWeight = FontWeight.Medium,
                        color = lc.scheme.onBackground,
                    )
                    val countLine = buildCountLine(placements.size, shortcuts.size)
                    if (countLine.isNotEmpty()) {
                        Text(
                            text = countLine,
                            style = textScale.scaled(MaterialTheme.typography.bodySmall),
                            color = lc.scheme.onBackground.copy(alpha = 0.72f),
                        )
                    }
                }
                if (placements.isNotEmpty() || shortcuts.isNotEmpty()) {
                    IconButton(onClick = { enterReorder() }) {
                        Icon(
                            imageVector = Icons.Outlined.UnfoldMore,
                            contentDescription = "Reorder widgets and shortcuts",
                            tint = lc.scheme.onBackground,
                        )
                    }
                }
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                        contentDescription = "Back to home",
                        tint = lc.scheme.onBackground,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }

            if (placements.isEmpty() && shortcuts.isEmpty()) {
                EmptyWidgetState(
                    modifier = Modifier.weight(1f),
                    textScale = textScale,
                    lc= lc.scheme,
                    onAdd = viewModel::openPicker,
                )
            } else {
                LazyColumnScrollbar(
                    state = listState,
                    settings = softScrollbarSettings,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (editingWidgetId != null || editingShortcutId != null) {
                                    Modifier.clickable(
                                        indication = null,
                                        interactionSource = emptyInteractionSource,
                                    ) {
                                        editingWidgetId = null
                                        editingShortcutId = null
                                    }
                                } else Modifier
                            ),
                        contentPadding = PaddingValues(
                            horizontal = 28.dp,
                            vertical = 8.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(placements, key = { item -> "w-${item.widgetId}" }) { placement ->
                            val preset = WidgetPreset.entries.firstOrNull {
                                it.heightDp == placement.heightDp
                            } ?: WidgetPreset.Auto

                            WidgetRow(
                                widgetId = placement.widgetId,
                                preset = preset,
                                viewModel = viewModel,
                                hostViewCache = hostViewCache,
                                isEditing = editingWidgetId == placement.widgetId,
                                onEnterEdit = {
                                    editingShortcutId = null
                                    editingWidgetId = placement.widgetId
                                },
                                onExitEdit = { editingWidgetId = null },
                                onSelectPreset = { p ->
                                    viewModel.resizeWidget(
                                        widgetId = placement.widgetId,
                                        preset = p,
                                    )
                                },
                                onRemove = {
                                    viewModel.removeWidget(placement.widgetId)
                                    hostViewCache.remove(placement.widgetId)
                                    editingWidgetId = null
                                },
                                pageActive = pageActive,
                            )
                        }

                        items(shortcuts, key = { item -> "s-${item.id}" }) { shortcut ->
                            Column(Modifier.fillMaxWidth()) {
                                if (editingShortcutId == shortcut.id) {
                                    InlineShortcutActionBar(
                                        onRemove = {
                                            viewModel.removeShortcut(shortcut.id)
                                            editingShortcutId = null
                                        },
                                        onClose = { editingShortcutId = null },
                                    )
                                }
                                ShortcutTile(
                                    shortcut = shortcut,
                                    onClick = { viewModel.launchShortcut(context, shortcut) },
                                    onLongClick = {
                                        editingWidgetId = null
                                        editingShortcutId = shortcut.id
                                    },
                                    isEditing = editingShortcutId == shortcut.id,
                                )
                            }
                        }

                        item(key = "add_row") {
                            AddWidgetRow(onClick = viewModel::openPicker, lc = lc.scheme, textScale = textScale)
                        }
                    }
                }
            }
        }

        if (isAdding) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.28f))
                    .clickable(
                        indication = null,
                        interactionSource = blockerInteractionSource,
                    ) { /* consume */ },
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Adding widget…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }

    if (pickerOpen) {
        WidgetPickerSheet(
            widgets = availableWidgets,
            shortcuts = availableShortcuts,
            settings = userSettings,
            isLoading = pickerLoading,
            onSelectWidget = { info ->
                isAdding = true
                viewModel.closePicker()
                val id = viewModel.allocateWidgetId()
                if (id == null) {
                    isAdding = false
                    return@WidgetPickerSheet
                }
                pendingId.value = id
                pendingId.providerInfo = info

                val bound = viewModel.bindWidget(id, info)
                if (bound) {
                    // DO NOT clear pendingId here. proceedAfterBind() may
                    // launch a config activity; when it returns, the config
                    // callback reads pendingId to know which widget to
                    // complete. Clearing now would wipe that link.
                    proceedAfterBind(
                        activity = activity,
                        widgetId = id,
                        info = info,
                        configLauncher = configLauncher,
                        onComplete = {
                            pendingId.clear()
                            viewModel.completeAdd(id, info)
                            isAdding = false
                        },
                        onCancel = {
                            pendingId.clear()
                            viewModel.cancelAdd(id)
                            isAdding = false
                        },
                    )
                } else {
                    val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, info.provider)
                    }
                    bindPermissionLauncher.launch(intent)
                }
            },
            onSelectShortcut = { info ->
                viewModel.closePicker()
                pendingShortcutSource = info
                val intent = Intent(Intent.ACTION_CREATE_SHORTCUT).apply {
                    setClassName(info.activityInfo.packageName, info.activityInfo.name)
                }
                shortcutLauncher.launch(intent)
            },
            onDismiss = viewModel::closePicker,
        )
    }
}

@Suppress("DEPRECATION")
private fun extractShortcutResult(data: Intent): Pair<Intent, String>? {
    val intent = data.getParcelableExtra<Intent>(Intent.EXTRA_SHORTCUT_INTENT)
        ?: return null
    val label = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME).orEmpty()
    return intent to label
}

private fun buildCountLine(widgetCount: Int, shortcutCount: Int): String {
    if (widgetCount == 0 && shortcutCount == 0) return ""
    val parts = mutableListOf<String>()
    if (widgetCount > 0) {
        parts.add("$widgetCount ${if (widgetCount == 1) "widget" else "widgets"}")
    }
    if (shortcutCount > 0) {
        parts.add("$shortcutCount ${if (shortcutCount == 1) "shortcut" else "shortcuts"}")
    }
    return parts.joinToString(" · ")
}

private fun proceedAfterBind(
    activity: Activity?,
    widgetId: Int,
    info: AppWidgetProviderInfo,
    configLauncher: androidx.activity.result.ActivityResultLauncher<Intent>,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
) {
    val configure = info.configure
    if (configure == null) {
        debugPrint.d(TAG, "[WidgetFlow] proceedAfterBind id=$widgetId no configure → onComplete")
        onComplete()
        return
    }
    if (activity == null) {
        debugPrint.d(TAG, "[WidgetFlow] proceedAfterBind id=$widgetId activity null → onComplete")
        onComplete()
        return
    }

    val isExported = try {
        activity.packageManager.getActivityInfo(configure, 0).exported
    } catch (_: Exception) {
        false
    }

    if (!isExported) {
        debugPrint.d(TAG, "[WidgetFlow] proceedAfterBind id=$widgetId not exported → onComplete")
        onComplete()
        return
    }

    val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
        component = configure
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
    }

    try {
        debugPrint.d(TAG, "[WidgetFlow] proceedAfterBind id=$widgetId launching config")
        configLauncher.launch(intent)
    } catch (_: SecurityException) {
        debugPrint.w(TAG, "[WidgetFlow] proceedAfterBind id=$widgetId config SecurityException")
        onComplete()
    }
}

private class PendingWidgetId {
    var value: Int? = null
    var providerInfo: AppWidgetProviderInfo? = null
    fun clear() {
        value = null
        providerInfo = null
    }
}