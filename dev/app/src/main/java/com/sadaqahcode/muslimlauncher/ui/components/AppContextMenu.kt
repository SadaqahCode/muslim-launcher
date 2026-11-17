package com.sadaqahcode.muslimlauncher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.data.launcher.shapeFor
import com.sadaqahcode.muslimlauncher.domain.model.AppCategory
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.ProvideLegibleColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AppContextMenu(
    app: LauncherApp,
    isFavorite: Boolean,
    isInDock: Boolean,
    settings: UserSettings,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleDock: () -> Unit,
    onCategorySelect: (AppCategory) -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
) {
    val themeIsDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.94f),
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        ProvideLegibleColors(wallpaperIsDark = themeIsDark) {
            val lc = LocalLegibleColors.current
            val c = lc.scheme

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Image(
                        bitmap = app.icon,
                        contentDescription = app.label,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(shapeFor(settings.iconShape)),
                    )
                    Column {
                        Text(
                            text = app.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = c.onBackground,
                        )
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = c.onSurfaceVariant,
                        )
                    }
                }

                HorizontalDivider(color = c.outline.copy(alpha = 0.3f))

                ToggleRow(
                    title = "Pin to home",
                    subtitle = "Max 8",
                    checked = isFavorite,
                    onCheckedChange = { onToggleFavorite() },
                )

                ToggleRow(
                    title = "Keep in dock",
                    subtitle = "Max 8 · ${settings.dockSlots} visible",
                    checked = isInDock,
                    onCheckedChange = { onToggleDock() },
                )

                HorizontalDivider(color = c.outline.copy(alpha = 0.3f))

                Text(
                    text = "What does this app mean for your heart?",
                    style = MaterialTheme.typography.titleSmall,
                    color = c.onBackground,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AppCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = app.category == cat,
                            onClick = { onCategorySelect(cat) },
                            label = { Text(cat.displayName) },
                        )
                    }
                }
                Text(
                    text = "A suggestion from the launcher. Tap a tag to make it yours.",
                    style = MaterialTheme.typography.bodySmall,
                    color = c.onSurfaceVariant,
                )

                HorizontalDivider(color = c.outline.copy(alpha = 0.3f))

                TextButton(onClick = onAppInfo, modifier = Modifier.fillMaxWidth()) {
                    Text("App info", color = c.onBackground)
                }
                TextButton(onClick = onUninstall, modifier = Modifier.fillMaxWidth()) {
                    Text("Uninstall", color = c.error)
                }
                Spacer(Modifier.width(1.dp))
            }
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val c = LocalLegibleColors.current.scheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = c.onBackground)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = c.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}