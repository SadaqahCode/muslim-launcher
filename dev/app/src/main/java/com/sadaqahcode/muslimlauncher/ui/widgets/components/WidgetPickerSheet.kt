package com.sadaqahcode.muslimlauncher.ui.widgets.components

import android.appwidget.AppWidgetProviderInfo
import android.content.pm.ResolveInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.components.DockIcon
import com.sadaqahcode.muslimlauncher.ui.components.PinnedAppItem
import com.sadaqahcode.muslimlauncher.ui.widgets.helper.rememberAppIcon
import com.sadaqahcode.muslimlauncher.ui.widgets.helper.rememberShortcutIcon
import com.sadaqahcode.muslimlauncher.ui.widgets.helper.rememberWidgetPreview
import com.sadaqahcode.muslimlauncher.ui.widgets.helper.widgetCellAspectRatio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

private sealed interface PickerItem {
    val packageName: String
    val label: String

    data class Widget(
        val info: AppWidgetProviderInfo,
        override val label: String,
    ) : PickerItem {
        override val packageName: String = info.provider.packageName
    }

    data class Shortcut(
        val info: ResolveInfo,
        override val label: String,
    ) : PickerItem {
        override val packageName: String = info.activityInfo.packageName
    }
}

private data class PickerGroup(
    val packageName: String,
    val label: String,
    val items: List<PickerItem>,
)

private val WIDGET_TILE_MAX_HEIGHT = 220.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WidgetPickerSheet(
    widgets: List<AppWidgetProviderInfo>,
    shortcuts: List<ResolveInfo>,
    settings: UserSettings,
    isLoading: Boolean,
    onSelectWidget: (AppWidgetProviderInfo) -> Unit,
    onSelectShortcut: (ResolveInfo) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { sheetState.expand() }

    val softScrollbarSettings = ScrollbarSettings.Default.copy(
        thumbThickness = 4.dp,
        thumbUnselectedColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
        thumbSelectedColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f),
        scrollbarPadding = 0.dp,
    )

    // Build groups off the main thread. `null` = still loading, empty = no
    // items on the device. The previous synchronous version blocked the sheet's
    // first frame by ~50–150 ms (50+ binder calls to PackageManager).
    val groupsState by produceState<List<PickerGroup>?>(
        initialValue = null,
        widgets,
        shortcuts,
    ) {
        value = withContext(Dispatchers.IO) {
            val map = mutableMapOf<String, MutableList<PickerItem>>()
            widgets.forEach { info ->
                val label = info.loadLabel(pm).toString()
                map.getOrPut(info.provider.packageName) { mutableListOf() }
                    .add(PickerItem.Widget(info, label))
            }
            shortcuts.forEach { info ->
                val label = info.loadLabel(pm).toString()
                map.getOrPut(info.activityInfo.packageName) { mutableListOf() }
                    .add(PickerItem.Shortcut(info, label))
            }
            map
                .map { (pkg, items) ->
                    // Pre-load the group header label here on IO. GroupSection
                    // then reads it directly instead of making its own binder
                    // call on the main thread during composition.
                    val label = runCatching {
                        pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
                    }.getOrDefault(pkg)
                    PickerGroup(pkg, label, items)
                }
                .sortedBy { it.label.lowercase() }
        }
    }

    var expandedPackage by rememberSaveable { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.94f),
        contentColor = MaterialTheme.colorScheme.onBackground,
        tonalElevation = 0.dp,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        contentWindowInsets = { WindowInsets.navigationBars },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Text(
                    "Add to widget page",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                )
                Text(
                    "Pick an app to see what it offers.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            LazyColumnScrollbar(
                state = listState,
                settings = softScrollbarSettings,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 4.dp,
                        end = 20.dp,
                        bottom = 16.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    val readyGroups = groupsState
                    when {
                        isLoading || readyGroups == null -> {
                            item(key = "loading") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 48.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 2.dp,
                                    )
                                }
                            }
                        }

                        readyGroups.isEmpty() -> {
                            item(key = "empty") {
                                Text(
                                    "Nothing available on this device.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 24.dp),
                                )
                            }
                        }

                        else -> {
                            items(readyGroups, key = { it.packageName }) { group ->
                                val isExpanded = expandedPackage == group.packageName
                                GroupSection(
                                    group = group,
                                    settings = settings,
                                    isExpanded = isExpanded,
                                    onToggle = {
                                        expandedPackage = if (isExpanded) null else group.packageName
                                    },
                                    onSelectWidget = onSelectWidget,
                                    onSelectShortcut = onSelectShortcut,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupSection(
    group: PickerGroup,
    settings: UserSettings,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onSelectWidget: (AppWidgetProviderInfo) -> Unit,
    onSelectShortcut: (ResolveInfo) -> Unit,
) {
    // `group.label` was pre-loaded on IO. No more `getApplicationLabel`
    // binder call on the main thread during composition.
    val appLabel = group.label

    val shapedAppIcon = rememberAppIcon(group.packageName, settings)
    val launcherApp: LauncherApp? = remember(group.packageName, shapedAppIcon, appLabel) {
        shapedAppIcon?.let {
            LauncherApp(
                packageName = group.packageName,
                label = appLabel,
                icon = it,
            )
        }
    }

    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DockIcon(
                app = launcherApp,
                onClick = onToggle,
                onLongClick = onToggle,
                settings = settings,
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = appLabel,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = settings.textSize.sp,
                    ),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${group.items.size} ${if (group.items.size == 1) "item" else "items"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }

        if (isExpanded) {
            val widgetItems = group.items.filterIsInstance<PickerItem.Widget>()
            val shortcutItems = group.items.filterIsInstance<PickerItem.Shortcut>()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                widgetItems.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        if (pair.size == 1) {
                            Box(Modifier.fillMaxWidth()) {
                                WidgetGridTile(
                                    widget = pair.first(),
                                    settings = settings,
                                    onClick = { onSelectWidget(pair.first().info) },
                                )
                            }
                        } else {
                            pair.forEach { widget ->
                                Box(Modifier.weight(1f)) {
                                    WidgetGridTile(
                                        widget = widget,
                                        settings = settings,
                                        onClick = { onSelectWidget(widget.info) },
                                    )
                                }
                            }
                        }
                    }
                }

                if (shortcutItems.isNotEmpty()) {
                    shortcutItems.chunked(4).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            row.forEach { shortcut ->
                                val icon = rememberShortcutIcon(shortcut.info, settings)
                                if (icon != null) {
                                    val app = remember(shortcut.packageName, shortcut.label, icon) {
                                        LauncherApp(
                                            packageName = shortcut.packageName,
                                            label = shortcut.label,
                                            icon = icon,
                                        )
                                    }
                                    Box(Modifier.weight(1f)) {
                                        PinnedAppItem(
                                            app = app,
                                            onClick = { onSelectShortcut(shortcut.info) },
                                            onLongClick = {},
                                            settings = settings,
                                        )
                                    }
                                } else {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                            repeat(4 - row.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetGridTile(
    widget: PickerItem.Widget,
    settings: UserSettings,
    onClick: () -> Unit,
) {
    val info = widget.info
    val preview = rememberWidgetPreview(info)
        ?: rememberAppIcon(info.provider.packageName, settings)
    val labelFontSize = settings.textSize.sp

    val aspectRatio = remember(preview, info.provider.flattenToString()) {
        val fromPreview = preview
            ?.takeIf { it.width > 0 && it.height > 0 }
            ?.let { it.width.toFloat() / it.height.toFloat() }
        val raw = fromPreview ?: widgetCellAspectRatio(info)
        raw.coerceIn(0.2f, 5f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .heightIn(max = WIDGET_TILE_MAX_HEIGHT)
                .aspectRatio(aspectRatio, matchHeightConstraintsFirst = true)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center,
        ) {
            if (preview != null) {
                Image(
                    bitmap = preview,
                    contentDescription = widget.label,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Text(
            text = widget.label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = labelFontSize),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}