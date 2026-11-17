package com.sadaqahcode.muslimlauncher.ui.widgets

import android.appwidget.AppWidgetProviderInfo
import android.content.pm.ResolveInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WidgetPickerSheet(
    widgets: List<AppWidgetProviderInfo>,
    shortcuts: List<ResolveInfo>,
    onSelectWidget: (AppWidgetProviderInfo) -> Unit,
    onSelectShortcut: (ResolveInfo) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Text(
                "Add to widget page",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Text(
                "Widgets and shortcuts from apps on this device.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (widgets.isNotEmpty()) {
                    item(key = "widget_header") {
                        SectionHeader("Widgets")
                    }
                    items(widgets, key = { it.provider.flattenToString() }) { info ->
                        WidgetRow(info = info, onClick = { onSelectWidget(info) })
                    }
                }

                if (shortcuts.isNotEmpty()) {
                    item(key = "shortcut_header") {
                        SectionHeader("Shortcuts")
                    }
                    items(shortcuts, key = {
                        "${it.activityInfo.packageName}/${it.activityInfo.name}"
                    }) { info ->
                        ShortcutRow(info = info, onClick = { onSelectShortcut(info) })
                    }
                }

                if (widgets.isEmpty() && shortcuts.isEmpty()) {
                    item {
                        Text(
                            "Nothing available on this device.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 24.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
    )
}

@Composable
private fun WidgetRow(
    info: AppWidgetProviderInfo,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val label = info.loadLabel(pm).toString()
    val appLabel = runCatching {
        pm.getApplicationLabel(pm.getApplicationInfo(info.provider.packageName, 0)).toString()
    }.getOrDefault(info.provider.packageName)
    val icon = runCatching {
        info.loadIcon(context, 0).toBitmap(96, 96).asImageBitmap()
    }.getOrNull()

    PickerRow(label = label, appLabel = appLabel, icon = icon, onClick = onClick)
}

@Composable
private fun ShortcutRow(
    info: ResolveInfo,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val label = info.loadLabel(pm).toString()
    val appLabel = runCatching {
        pm.getApplicationLabel(pm.getApplicationInfo(info.activityInfo.packageName, 0)).toString()
    }.getOrDefault(info.activityInfo.packageName)
    val icon = runCatching {
        info.loadIcon(pm).toBitmap(96, 96).asImageBitmap()
    }.getOrNull()

    PickerRow(label = label, appLabel = appLabel, icon = icon, onClick = onClick)
}

@Composable
private fun PickerRow(
    label: String,
    appLabel: String,
    icon: androidx.compose.ui.graphics.ImageBitmap?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Image(
                bitmap = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
            )
        } else {
            Box(Modifier.size(40.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = appLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}