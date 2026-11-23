package com.sadaqahcode.muslimlauncher.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.data.local.launcher.shapeFor
import com.sadaqahcode.muslimlauncher.domain.model.AppCategory
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.domain.model.icon
import com.sadaqahcode.muslimlauncher.ui.theme.GlassEmphasis
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.ProvideLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.glass

private val GroupShape = RoundedCornerShape(24.dp)
private val TagShape = RoundedCornerShape(16.dp)

enum class MenuPage { Main, Tag }

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

    var page by remember(app.packageName) { mutableStateOf(MenuPage.Main) }

    ScrimDialog(
        onDismiss = onDismiss,
    ) {
        BackHandler(enabled = page == MenuPage.Tag) {
            page = MenuPage.Main
        }

        ProvideLegibleColors(wallpaperIsDark = themeIsDark) {
            val lc = LocalLegibleColors.current
            val c = lc.scheme

            Crossfade(
                targetState = page,
                modifier = Modifier.animateContentSize(),
                label = "menuPage",
            ) { current ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    when (current) {
                        MenuPage.Main -> MenuMain(
                            app = app,
                            settings = settings,
                            isFavorite = isFavorite,
                            isInDock = isInDock,
                            onToggleFavorite = onToggleFavorite,
                            onToggleDock = onToggleDock,
                            onOpenTags = { page = MenuPage.Tag },
                            onAppInfo = onAppInfo,
                            onUninstall = onUninstall,
                            onClose = onDismiss
                        )

                        MenuPage.Tag -> MenuTag(
                            app = app,
                            onBack = { page = MenuPage.Main },
                            onCategorySelect = { cat ->
                                onCategorySelect(cat)
                                page = MenuPage.Main
                            },
                        )
                    }
                }
            }
        }
    }
}
}

// Pages
@Composable
private fun MenuMain(
    app: LauncherApp,
    settings: UserSettings,
    isFavorite: Boolean,
    isInDock: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleDock: () -> Unit,
    onOpenTags: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
    onClose: () -> Unit,
) {
    val c = LocalLegibleColors.current.scheme

    Spacer(Modifier.height(8.dp))

    // App
    Row(
        modifier = Modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            bitmap = app.icon,
            contentDescription = app.label,
            modifier = Modifier
                .size(48.dp)
                .clip(shapeFor(settings.iconShape)),
        )
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = app.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = c.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = c.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(
            onClick = onClose,
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = c.onSurfaceVariant,
            )
        }
    }

    // Pin and dock
    GlassGroup {
        ToggleRow(
            title = "Pin to home",
            subtitle = "Max ${settings.maxPinned}",
            checked = isFavorite,
            onCheckedChange = { onToggleFavorite() },
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "What does this app mean for your heart?",
            style = MaterialTheme.typography.titleSmall,
            color = c.onBackground,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        GlassGroup {
            CurrentTagRow(category = app.category, onClick = onOpenTags)
        }
    }

    // Actions
    GlassGroup {
        ActionRow(text = "App info", color = c.onBackground, onClick = onAppInfo)
        RowDivider()
        ActionRow(text = "Uninstall", color = c.error, onClick = onUninstall)
    }

    Spacer(Modifier.height(8.dp))
}

@Composable
private fun MenuTag(
app: LauncherApp,
onBack: () -> Unit,
onCategorySelect: (AppCategory) -> Unit,
) {
val c = LocalLegibleColors.current.scheme

Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    TextButton(
        onClick = onBack,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = c.onBackground,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text("Back", color = c.onBackground)
    }
    Text(
        text = "What does this app mean for your heart?",
        style = MaterialTheme.typography.titleMedium,
        color = c.onBackground,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}

Column(
    modifier = Modifier.selectableGroup(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
) {
    AppCategory.entries.forEach { cat ->
        TagRow(
            category = cat,
            selected = app.category == cat,
            onClick = { onCategorySelect(cat) },
        )
    }
}

Text(
    text = "A suggestion from the launcher. Tap a tag to make it yours.",
    style = MaterialTheme.typography.bodySmall,
    color = c.onSurfaceVariant,
    modifier = Modifier.padding(horizontal = 4.dp),
)

Spacer(Modifier.height(8.dp))
}

// Pieces

@Composable
private fun GlassGroup(content: @Composable ColumnScope.() -> Unit) {
Column(
    modifier = Modifier
        .fillMaxWidth()
        .glass(GroupShape),
    content = content,
)
}

@Composable
private fun RowDivider() {
val c = LocalLegibleColors.current.scheme
HorizontalDivider(
    modifier = Modifier.padding(horizontal = 16.dp),
    color = c.outline.copy(alpha = 0.2f),
)
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
    modifier = Modifier
        .fillMaxWidth()
        .toggleable(
            value = checked,
            role = Role.Switch,
            onValueChange = onCheckedChange,
        )
        .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
) {
    Column(Modifier.weight(1f)) {
        Text(title, style = MaterialTheme.typography.bodyLarge, color = c.onBackground)
        Text(
            subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = c.onSurfaceVariant,
        )
    }
    Switch(checked = checked, onCheckedChange = null)
}
}

@Composable
private fun CategoryTile(category: AppCategory, selected: Boolean) {
val c = LocalLegibleColors.current.scheme
Box(
    modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(c.onBackground.copy(alpha = if (selected) 0.12f else 0.06f)),
    contentAlignment = Alignment.Center,
) {
    Icon(
        imageVector = category.icon,
        contentDescription = null,
        tint = if (selected) c.onBackground else c.onSurfaceVariant,
        modifier = Modifier.size(22.dp),
    )
}
}

@Composable
private fun CurrentTagRow(category: AppCategory, onClick: () -> Unit) {
val c = LocalLegibleColors.current.scheme

Row(
    modifier = Modifier
        .fillMaxWidth()
        .clickable(onClickLabel = "Change tag", role = Role.Button, onClick = onClick)
        .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
) {
    CategoryTile(category = category, selected = true)
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = c.onBackground,
        )
        Text(
            text = category.hint,
            style = MaterialTheme.typography.bodySmall,
            color = c.onSurfaceVariant,
        )
    }
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = c.onSurfaceVariant,
        modifier = Modifier.size(20.dp),
    )
}
}

@Composable
private fun TagRow(
category: AppCategory,
selected: Boolean,
onClick: () -> Unit,
) {
val c = LocalLegibleColors.current.scheme

Row(
    modifier = Modifier
        .fillMaxWidth()
        .glass(
            shape = TagShape,
            emphasis = if (selected) GlassEmphasis.Selected else GlassEmphasis.Regular,
        )
        .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
        .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
) {
    CategoryTile(category = category, selected = selected)
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = c.onBackground,
        )
        Text(
            text = category.hint,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) c.onBackground.copy(alpha = 0.8f) else c.onSurfaceVariant,
        )
    }
    RadioButton(selected = selected, onClick = null)
}
}

@Composable
private fun ActionRow(text: String, color: Color, onClick: () -> Unit) {
Text(
    text = text,
    style = MaterialTheme.typography.bodyLarge,
    color = color,
    modifier = Modifier
        .fillMaxWidth()
        .clickable(role = Role.Button, onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 16.dp),
)
}