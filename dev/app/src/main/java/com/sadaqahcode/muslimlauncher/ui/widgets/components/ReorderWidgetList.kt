package com.sadaqahcode.muslimlauncher.ui.widgets.components

import android.appwidget.AppWidgetHostView
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.data.local.ShortcutEntity
import com.sadaqahcode.muslimlauncher.data.local.WidgetEntity
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.widgets.WidgetPageViewModel
import com.sadaqahcode.muslimlauncher.ui.widgets.WidgetPreset

@Composable
internal fun ReorderWidgetList(
    textScale: AppTextSize,
    placements: List<WidgetEntity>,
    shortcuts: List<ShortcutEntity>,
    viewModel: WidgetPageViewModel,
    hostViewCache: MutableMap<Int, AppWidgetHostView>,
    onDone: (widgetIds: List<Int>, shortcutIds: List<String>) -> Unit,
    onRemoved: (widgetIds: List<Int>, shortcutIds: List<String>) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val lc = LocalLegibleColors.current

    var localWidgetOrder by remember(placements) { mutableStateOf(placements) }
    var localShortcutOrder by remember(shortcuts) { mutableStateOf(shortcuts) }
    var removedWidgetIds by remember { mutableStateOf(setOf<Int>()) }
    var removedShortcutIds by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(placements) {
        if (removedWidgetIds.isEmpty()) localWidgetOrder = placements
    }
    LaunchedEffect(shortcuts) {
        if (removedShortcutIds.isEmpty()) localShortcutOrder = shortcuts
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    start = 28.dp,
                    end = 16.dp,
                    top = if (isLandscape) 4.dp else 16.dp,
                    bottom = if (isLandscape) 8.dp else 16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Reorder",
                    style = textScale.scaled(MaterialTheme.typography.titleLarge),
                    fontWeight = FontWeight.Medium,
                    color = lc.scheme.onBackground,
                )
                Text(
                    text = "Use arrows to reorder · tap trash to remove",
                    style = textScale.scaled(MaterialTheme.typography.bodySmall),
                    color = lc.scheme.onBackground.copy(alpha = 0.72f),
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            TextButton(
                onClick = {
                    onRemoved(
                        removedWidgetIds.toList(),
                        removedShortcutIds.toList(),
                    )
                    onDone(
                        localWidgetOrder.map { it.widgetId },
                        localShortcutOrder.map { it.id },
                    )
                },
            ) {
                Text(
                    "Done",
                    color = lc.scheme.primary,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .padding(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Widgets ─────────────────────────────────────────────
            localWidgetOrder.forEachIndexed { index, placement ->
                key("w-${placement.widgetId}") {
                    val preset = WidgetPreset.entries.firstOrNull {
                        it.heightDp == placement.heightDp
                    } ?: WidgetPreset.Auto

                    val isFirst = index == 0
                    val isLast = index == localWidgetOrder.lastIndex

                    ReorderRow(
                        onMoveUp = {
                            if (!isFirst) {
                                localWidgetOrder = localWidgetOrder.toMutableList().apply {
                                    val tmp = this[index]
                                    this[index] = this[index - 1]
                                    this[index - 1] = tmp
                                }
                            }
                        },
                        onMoveDown = {
                            if (!isLast) {
                                localWidgetOrder = localWidgetOrder.toMutableList().apply {
                                    val tmp = this[index]
                                    this[index] = this[index + 1]
                                    this[index + 1] = tmp
                                }
                            }
                        },
                        onDelete = {
                            removedWidgetIds = removedWidgetIds + placement.widgetId
                            localWidgetOrder = localWidgetOrder.filterNot {
                                it.widgetId == placement.widgetId
                            }
                        },
                        canMoveUp = !isFirst,
                        canMoveDown = !isLast,
                    ) {
                        WidgetRow(
                            widgetId = placement.widgetId,
                            preset = preset,
                            viewModel = viewModel,
                            hostViewCache = hostViewCache,
                            isEditing = false,
                            onEnterEdit = { },
                            onExitEdit = { },
                            onSelectPreset = { },
                            onRemove = { },
                            pageActive = true,
                        )
                    }
                }
            }

            // ── Shortcuts ───────────────────────────────────────────
            localShortcutOrder.forEachIndexed { index, shortcut ->
                key("s-${shortcut.id}") {
                    val isFirst = index == 0
                    val isLast = index == localShortcutOrder.lastIndex

                    ReorderRow(
                        onMoveUp = {
                            if (!isFirst) {
                                localShortcutOrder = localShortcutOrder.toMutableList().apply {
                                    val tmp = this[index]
                                    this[index] = this[index - 1]
                                    this[index - 1] = tmp
                                }
                            }
                        },
                        onMoveDown = {
                            if (!isLast) {
                                localShortcutOrder = localShortcutOrder.toMutableList().apply {
                                    val tmp = this[index]
                                    this[index] = this[index + 1]
                                    this[index + 1] = tmp
                                }
                            }
                        },
                        onDelete = {
                            removedShortcutIds = removedShortcutIds + shortcut.id
                            localShortcutOrder = localShortcutOrder.filterNot {
                                it.id == shortcut.id
                            }
                        },
                        canMoveUp = !isFirst,
                        canMoveDown = !isLast,
                    ) {
                        ShortcutTile(
                            shortcut = shortcut,
                            onClick = { },
                            onLongClick = { },
                            isEditing = false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReorderRow(
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Up / Down stack
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FilledTonalIconButton(
                onClick = onMoveUp,
                enabled = canMoveUp,
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Move up",
                    modifier = Modifier.size(12.dp),
                )
            }
            FilledTonalIconButton(
                onClick = onMoveDown,
                enabled = canMoveDown,
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Move down",
                    modifier = Modifier.size(12.dp),
                )
            }
        }
        Spacer(Modifier.width(2.dp))
        Box(Modifier.weight(1f)) { content() }
        Spacer(Modifier.width(2.dp))
        FilledTonalIconButton(
            onClick = onDelete,
            modifier = Modifier.size(38.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ),
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove",
                modifier = Modifier.size(14.dp),
            )
        }
    }
}