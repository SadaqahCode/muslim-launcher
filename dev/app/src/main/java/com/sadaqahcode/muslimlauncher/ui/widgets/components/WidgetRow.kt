package com.sadaqahcode.muslimlauncher.ui.widgets.components

import android.appwidget.AppWidgetHostView
import android.os.Build
import android.util.SizeF
import android.view.View
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.flagodna.debugprint.debugPrint
import com.sadaqahcode.muslimlauncher.ui.widgets.LauncherAppWidgetHostView
import com.sadaqahcode.muslimlauncher.ui.widgets.WidgetPageViewModel
import com.sadaqahcode.muslimlauncher.ui.widgets.WidgetPreset

private const val TAG = "WidgetRow"

@Composable
internal fun WidgetRow(
    widgetId: Int,
    preset: WidgetPreset,
    viewModel: WidgetPageViewModel,
    hostViewCache: MutableMap<Int, AppWidgetHostView>,
    isEditing: Boolean,
    onEnterEdit: () -> Unit,
    onExitEdit: () -> Unit,
    onSelectPreset: (WidgetPreset) -> Unit,
    onRemove: () -> Unit,
    pageActive: Boolean = true,
) {
    val errorColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val emptyInteractionSource = remember { MutableInteractionSource() }
    val density = LocalDensity.current

    LaunchedEffect(widgetId, preset) {
        debugPrint.d(TAG, "[WidgetFlow] composed id=$widgetId preset=${preset.label} isEditing=$isEditing")
    }

    LaunchedEffect(pageActive) {
        if (!pageActive) {
            (hostViewCache[widgetId] as? LauncherAppWidgetHostView)?.cancelLongPress()
        }
    }

    // Auto = exact minHeight from the widget (converted px → dp)
    // Small / Medium / Large = fixed height
    val targetHeightDp = remember(widgetId, preset) {
        if (preset.heightDp > 0) preset.heightDp
        else viewModel.autoHeightDpFor(widgetId)
    }

    Column(Modifier.fillMaxWidth()) {
        if (isEditing) {
            InlineActionBar(
                currentPreset = preset,
                onSelectPreset = onSelectPreset,
                onRemove = onRemove,
                onClose = onExitEdit,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isEditing) {
                        Modifier
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp),
                            )
                            .clickable(
                                indication = null,
                                interactionSource = emptyInteractionSource,
                            ) { onExitEdit() }
                    } else Modifier
                )
                .requiredHeight(targetHeightDp.dp)
                .clipToBounds()
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { size ->
                        val hostView = hostViewCache[widgetId]
                            ?: return@onSizeChanged

                        val widthDp = with(density) { size.width.toDp().value }
                        val heightDp = with(density) { size.height.toDp().value }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // Modern non-deprecated API (Android 12+)
                            hostView.updateAppWidgetSize(
                                android.os.Bundle(),
                                listOf(SizeF(widthDp, heightDp))
                            )
                        } else {
                            // Fallback for Android 11 and below
                            @Suppress("DEPRECATION")
                            hostView.updateAppWidgetSize(
                                null,
                                widthDp.toInt(),
                                heightDp.toInt(),
                                widthDp.toInt(),
                                heightDp.toInt()
                            )
                        }
                    },
                factory = { ctx ->
                    val view = viewModel.createViewFor(ctx, widgetId, errorColor)
                    if (view == null) {
                        debugPrint.e(TAG, "[WidgetFlow] id=$widgetId factory got NULL view ✗")
                        View(ctx)
                    } else {
                        debugPrint.d(
                            TAG,
                            "[WidgetFlow] id=$widgetId factory got view=${view.javaClass.simpleName} ✓ " +
                                    "targetHeightDp=$targetHeightDp"
                        )
                        view.setOnLongClickListener {
                            if (!isEditing) onEnterEdit()
                            true
                        }
                        hostViewCache[widgetId] = view
                        view
                    }
                },
                update = { /* size is reported via onSizeChanged */ },
            )
        }
    }
}

@Composable
private fun InlineActionBar(
    currentPreset: WidgetPreset,
    onSelectPreset: (WidgetPreset) -> Unit,
    onRemove: () -> Unit,
    onClose: () -> Unit,
) {
    var sizeMenuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: size / preset selector
        Box {
            Surface(
                onClick = { sizeMenuOpen = true },
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Row(
                    modifier = Modifier.padding(
                        start = 14.dp, end = 10.dp, top = 8.dp, bottom = 8.dp,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = currentPreset.label,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Spacer(Modifier.size(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            DropdownMenu(
                expanded = sizeMenuOpen,
                onDismissRequest = { sizeMenuOpen = false },
                modifier = Modifier.width(180.dp),
            ) {
                WidgetPreset.entries.forEach { p ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = p.label,
                                fontWeight = if (p == currentPreset) FontWeight.Medium
                                else FontWeight.Normal,
                            )
                        },
                        onClick = {
                            onSelectPreset(p)
                            sizeMenuOpen = false
                        },
                        trailingIcon = if (p == currentPreset) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        } else null,
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Right: [Delete] [Close]
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = onRemove,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove widget",
                    modifier = Modifier.padding(8.dp).size(20.dp),
                )
            }

            Surface(
                onClick = onClose,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Done",
                    modifier = Modifier.padding(8.dp).size(20.dp),
                )
            }
        }
    }
}