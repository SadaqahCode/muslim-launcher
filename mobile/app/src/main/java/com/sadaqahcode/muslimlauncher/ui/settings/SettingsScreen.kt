package com.sadaqahcode.muslimlauncher.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadaqahcode.muslimlauncher.domain.model.AppIconShape
import com.sadaqahcode.muslimlauncher.domain.model.AppIconSize
import com.sadaqahcode.muslimlauncher.domain.model.AppItemLayout
import com.sadaqahcode.muslimlauncher.domain.model.AppLanguage
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.domain.model.ColorSource
import com.sadaqahcode.muslimlauncher.domain.model.DarkMode
import com.sadaqahcode.muslimlauncher.domain.model.DrawerLayout
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperMode
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperTextMode
import com.sadaqahcode.muslimlauncher.ui.theme.GlassEmphasis
import com.sadaqahcode.muslimlauncher.ui.theme.Wallpaper
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import kotlin.math.abs
import androidx.core.graphics.toColorInt
import com.sadaqahcode.muslimlauncher.ui.common.layout.PageMaxWidthScreen

private val ScreenGutter = 20.dp
private val RowGutter = 16.dp
private val GroupShape = RoundedCornerShape(24.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onClose: () -> Unit,
    onOpenLicenses: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val s by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val wallpaperApplying by viewModel.wallpaperApplying.collectAsStateWithLifecycle()

    val schemeBackground = "#%06X".format(
        0xFFFFFF and MaterialTheme.colorScheme.background.toArgb()
    )

    // Android 12+ is needed for both of these. Where it is missing, dynamic color is not
    val atLeastAndroid12 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            viewModel.setWallpaperImageUri(uri.toString())
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
    Scaffold(
        modifier = Modifier
            .widthIn(max = PageMaxWidthScreen).fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colors.background,
        contentColor = colors.onBackground,
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") },
                actions = {
                    IconButton(onClick = onOpenLicenses) {
                        Icon(Icons.Default.Policy, contentDescription = "Open Source Licenses")
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close settings")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background,
                    scrolledContainerColor = colors.background,
                    titleContentColor = colors.onBackground,
                    actionIconContentColor = colors.onBackground,
                ),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = ScreenGutter,
                end = ScreenGutter,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 48.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Language
            item(key = "language") {
                SettingsGroup("Language") {
                    ChoiceRow(
                        title = "App language",
                        hint = "Changes the language used throughout the app. " +
                                "Native names are shown so you can always find your own language.",
                        options = AppLanguage.entries,
                        selected = s.language,
                        label = { it.nativeName },
                        onSelect = viewModel::setLanguage,
                    )
                }
            }

            // Appearance
            item(key = "appearance") {
                SettingsGroup("Appearance") {
                    ChoiceRow(
                        title = "Theme",
                        hint = "Follow the system, or pin to light or dark.",
                        options = DarkMode.entries,
                        selected = s.darkMode,
                        label = {
                            when (it) {
                                DarkMode.Auto -> "Auto"
                                DarkMode.Light -> "Light"
                                DarkMode.Dark -> "Dark"
                            }
                        },
                        onSelect = viewModel::setDarkMode,
                    )
                    RowDivider()
                    ChoiceRow(
                        title = "Color source",
                        hint = "Where the palette comes from. " +
                                "Launcher uses the brand palette. " +
                                "Adaptive follows the phone's Material You colors. " +
                                "Custom builds a palette from a base color you pick.",
                        options = ColorSource.entries.filter {
                            it != ColorSource.AdaptivePhone || atLeastAndroid12
                        },
                        selected = s.colorSource,
                        label = {
                            when (it) {
                                ColorSource.LauncherDefault -> "Launcher"
                                ColorSource.AdaptivePhone -> "Adaptive"
                                ColorSource.CustomColor -> "Custom"
                            }
                        },
                        onSelect = viewModel::setColorSource,
                    )
                    if (s.colorSource == ColorSource.CustomColor) {
                        RowDivider()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = RowGutter, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            RowText(
                                title = "Base color",
                                hint = "The seed color used to derive every role — " +
                                        "primary, surface, containers, and so on. " +
                                        "Same algorithm Android uses for Material You, " +
                                        "so it works identically on any OEM.",
                            )
                            ColorSwatchGrid(
                                presets = ColorPalletePresets,
                                selectedHex = s.customSeedColor,
                                onSelect = viewModel::setCustomSeedColor,
                            )
                        }
                    }
                    RowDivider()
                    ChoiceRow(
                        title = "Text over wallpaper",
                        hint = "The foreground color of labels, hero text, and icons " +
                                "that sit directly on the wallpaper. " +
                                "Auto picks based on the wallpaper's brightness. " +
                                "Light or Dark forces it when the auto-detect gets it wrong.",
                        options = WallpaperTextMode.entries,
                        selected = s.wallpaperTextMode,
                        label = {
                            when (it) {
                                WallpaperTextMode.Auto -> "Auto"
                                WallpaperTextMode.Light -> "Light"
                                WallpaperTextMode.Dark -> "Dark"
                            }
                        },
                        onSelect = viewModel::setWallpaperTextMode,
                    )
                }
            }

            // Wallpaper
            item(key = "wallpaper") {
                SettingsGroup("Wallpaper") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.surfaceContainer),
                    ) {
                        Wallpaper(
                            settings = s,
                            modifier = Modifier.fillMaxSize(),
                        )
                        if (wallpaperApplying) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(colors.scrim.copy(alpha = 0.45f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Applying…",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                )
                            }
                        }
                    }

                    RowDivider()
                    ChoiceRow(
                        title = "Source",
                        hint = "What fills the space behind your app icons.",
                        options = WallpaperMode.entries,
                        selected = s.wallpaperMode,
                        label = {
                            when (it) {
                                WallpaperMode.System -> "System"
                                WallpaperMode.Color -> "Color"
                                WallpaperMode.Gradient -> "Gradient"
                                WallpaperMode.Image -> "Image"
                            }
                        },
                        onSelect = { mode ->
                            viewModel.setWallpaperMode(mode, schemeBackground)
                        },
                    )

                    RowDivider()
                    when (s.wallpaperMode) {
                        WallpaperMode.System -> {
                            RowText(
                                title = "System wallpaper",
                                hint = "Uses the wallpaper currently set in your device settings.",
                                modifier = Modifier.padding(
                                    horizontal = RowGutter,
                                    vertical = 14.dp
                                ),
                            )
                        }

                        WallpaperMode.Color -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = RowGutter, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                RowText(
                                    title = "Color",
                                    hint = "Dark swatches switch the theme to Dark, light swatches to " +
                                            "Light, so labels stay legible. You can still override " +
                                            "it with Theme above.",
                                )
                                ColorSwatchGrid(
                                    presets = ColorPresets,
                                    selectedHex = s.wallpaperColor,
                                    onSelect = viewModel::setWallpaperColor,
                                )
                            }
                        }

                        WallpaperMode.Gradient -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = RowGutter, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                RowText(
                                    title = "Gradient",
                                    hint = "Top to bottom, two colors. The theme follows the " +
                                            "gradient's average brightness so text stays readable.",
                                )
                                GradientSwatchGrid(
                                    presets = GradientPresets,
                                    selectedStart = s.wallpaperGradientStart,
                                    selectedEnd = s.wallpaperGradientEnd,
                                    onSelect = { start, end ->
                                        viewModel.setWallpaperGradient(start, end)
                                    },
                                )
                            }
                        }

                        WallpaperMode.Image -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = RowGutter, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                RowText(
                                    title = "Image",
                                    hint = "Pick a photo from your gallery.",
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Button(onClick = {
                                        imagePicker.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    }) {
                                        Icon(
                                            Icons.Outlined.Image,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                        )
                                        Text(
                                            text = if (s.wallpaperImageUri == null) "Pick image" else "Change image",
                                            modifier = Modifier.padding(start = 8.dp),
                                        )
                                    }
                                    if (s.wallpaperImageUri != null) {
                                        OutlinedButton(onClick = {
                                            viewModel.setWallpaperImageUri(null)
                                        }) {
                                            Icon(
                                                Icons.Outlined.Delete,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                            )
                                            Text(
                                                text = "Remove",
                                                modifier = Modifier.padding(start = 8.dp),
                                            )
                                        }
                                    }
                                }
                            }

                            RowDivider()
                            SwitchRow(
                                title = "Blur",
                                subtitle = if (atLeastAndroid12) {
                                    "Softens the image so icons stay legible."
                                } else {
                                    "Needs Android 12 or newer."
                                },
                                checked = s.wallpaperBlurEnabled && atLeastAndroid12,
                                enabled = atLeastAndroid12,
                                onCheckedChange = viewModel::setWallpaperBlurEnabled,
                            )
                            if (atLeastAndroid12 && s.wallpaperBlurEnabled) {
                                RowDivider()
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = RowGutter, vertical = 10.dp),
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Blur radius",
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f),
                                        )
                                        Text(
                                            text = "${s.wallpaperBlurRadius} dp",
                                            style = MaterialTheme.typography.bodyMedium
                                                .copy(fontFeatureSettings = "tnum"),
                                            color = colors.onSurfaceVariant,
                                        )
                                    }
                                    Slider(
                                        value = s.wallpaperBlurRadius.toFloat(),
                                        onValueChange = {
                                            viewModel.setWallpaperBlurRadius(it.toInt())
                                        },
                                        valueRange = 1f..50f,
                                        steps = 48,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Layout
            item(key = "layout") {
                SettingsGroup("Layout") {
                    ChoiceRow(
                        title = "Drawer layout",
                        hint = "How apps are listed when you open the drawer.",
                        options = DrawerLayout.entries,
                        selected = s.drawerLayout,
                        label = {
                            when (it) {
                                DrawerLayout.List -> "List"
                                DrawerLayout.Grid -> "Grid"
                                DrawerLayout.HorizontalScroll -> "Horizontal"
                            }
                        },
                        onSelect = viewModel::setDrawerLayout,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                text = "Extra space at top screens",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        Switch(
                            checked = s.paddingTopEnabled,
                            onCheckedChange = { viewModel.setPaddingTopEnabled(it) },
                        )
                    }
                }
            }

            // Icons & labels
            item(key = "icons") {
                SettingsGroup("Icons and labels") {
                    ChoiceRow(
                        title = "Icon size",
                        hint = "How large app icons appear on home, dock, and drawer.",
                        options = AppIconSize.entries,
                        selected = s.iconSize,
                        label = { it.name.humanize() },
                        onSelect = viewModel::setIconSize,
                    )
                    RowDivider()
                    ChoiceRow(
                        title = "Icon shape",
                        hint = "How app icons are clipped.",
                        options = AppIconShape.entries,
                        selected = s.iconShape,
                        label = { it.name.humanize() },
                        onSelect = viewModel::setIconShape,
                    )
                    RowDivider()
                    ChoiceRow(
                        title = "Text size",
                        hint = "Label size below or beside app icons.",
                        options = AppTextSize.entries,
                        selected = s.textSize,
                        label = { it.name.humanize() },
                        onSelect = viewModel::setTextSize,
                    )
                    RowDivider()
                    ChoiceRow(
                        title = "Label position",
                        hint = "Where the app name appears relative to the icon.",
                        options = AppItemLayout.entries,
                        selected = s.itemLayout,
                        label = {
                            when (it) {
                                AppItemLayout.TextBelow -> "Below"
                                AppItemLayout.TextBeside -> "Beside"
                                AppItemLayout.IconOnly -> "Icon only"
                            }
                        },
                        onSelect = viewModel::setItemLayout,
                    )
                }
            }

            item(key = "footer") {
                Text(
                    text = "Everything stays on this device. No accounts, no sync, no analytics.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                )
            }
        }
    }
    }
}

// Group and rows

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = RowGutter, bottom = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glass(GroupShape)
                .animateContentSize(),
            content = content,
        )
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = RowGutter),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
    )
}

@Composable
private fun RowText(
    title: String,
    hint: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (hint != null) {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    val alpha = if (enabled) 1f else 0.5f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .padding(horizontal = RowGutter, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Switch(checked = checked, onCheckedChange = null, enabled = enabled)
    }
}

@Composable
private fun <T> ChoiceRow(
    title: String,
    hint: String?,
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = RowGutter, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RowText(title, hint)
        if (options.size <= 4) {
            SegmentedControl(options, selected, label, onSelect)
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                options.forEach { option ->
                    FilterChip(
                        selected = option == selected,
                        onClick = { onSelect(option) },
                        label = { Text(label(option)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun IntChoiceRow(
    title: String,
    hint: String?,
    options: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    if (options.size <= 4) {
        ChoiceRow(
            title = title,
            hint = hint,
            options = options,
            selected = selected,
            label = { "$it" },
            onSelect = onSelect,
        )
    } else {
        StepperRow(title, hint, options, selected, onSelect)
    }
}

@Composable
private fun StepperRow(
    title: String,
    hint: String?,
    options: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    val index = options.indexOf(selected).takeIf { it >= 0 }
        ?: options.indices.minByOrNull { abs(options[it] - selected) }
        ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = RowGutter, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RowText(title, hint, Modifier.weight(1f))
        Spacer(Modifier.width(12.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(trackColor()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { onSelect(options[index - 1]) },
                enabled = index > 0,
                modifier = Modifier.semantics { contentDescription = "Decrease $title" },
            ) {
                Text("−", style = MaterialTheme.typography.titleLarge)
            }
            Text(
                text = "$selected",
                style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 28.dp),
            )
            IconButton(
                onClick = { onSelect(options[index + 1]) },
                enabled = index < options.lastIndex,
                modifier = Modifier.semantics { contentDescription = "Increase $title" },
            ) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun <T> SegmentedControl(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val index = options.indexOf(selected)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(trackColor())
            .padding(2.dp)
            .selectableGroup(),
    ) {
        val segmentWidth = maxWidth / options.size
        val thumbX by animateDpAsState(
            targetValue = segmentWidth * index.coerceAtLeast(0),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
            label = "segmentThumb",
        )

        if (index >= 0) {
            Box(
                modifier = Modifier
                    .offset(x = thumbX)
                    .width(segmentWidth)
                    .height(44.dp)
                    .glass(RoundedCornerShape(12.dp), GlassEmphasis.Selected),
            )
        }

        Row {
            options.forEachIndexed { i, option ->
                val isSelected = i == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .selectable(
                            selected = isSelected,
                            onClick = { onSelect(option) },
                            role = Role.RadioButton,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label(option),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) colors.onSurface else colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun trackColor(): Color {
    val c = MaterialTheme.colorScheme
    return c.onSurface.copy(alpha = if (c.background.luminance() < 0.5f) 0.10f else 0.07f)
}

// Swatch pickers
@Composable
private fun ColorSwatchGrid(
    presets: List<ColorPreset>,
    selectedHex: String,
    onSelect: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        presets.forEachIndexed { i, preset ->
            val color = runCatching {
                Color(android.graphics.Color.parseColor(preset.hex))
            }.getOrNull() ?: return@forEachIndexed
            SwatchFrame(
                selected = preset.hex.equals(selectedHex, ignoreCase = true),
                description = "Color ${i + 1} of ${presets.size}",
                outerShape = CircleShape,
                innerShape = CircleShape,
                fill = Modifier.background(color),
                onClick = { onSelect(preset.hex) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GradientSwatchGrid(
    presets: List<GradientPreset>,
    selectedStart: String,
    selectedEnd: String,
    onSelect: (String, String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        presets.forEachIndexed { i, preset ->
            val start = runCatching {
                Color(preset.start.toColorInt())
            }.getOrNull() ?: return@forEachIndexed
            val end = runCatching {
                Color(preset.end.toColorInt())
            }.getOrNull() ?: return@forEachIndexed
            SwatchFrame(
                selected = preset.start.equals(selectedStart, ignoreCase = true) &&
                        preset.end.equals(selectedEnd, ignoreCase = true),
                description = "Gradient ${i + 1} of ${presets.size}",
                outerShape = RoundedCornerShape(16.dp),
                innerShape = RoundedCornerShape(11.dp),
                fill = Modifier.background(Brush.verticalGradient(listOf(start, end))),
                onClick = { onSelect(preset.start, preset.end) },
            )
        }
    }
}

@Composable
private fun SwatchFrame(
    selected: Boolean,
    description: String,
    outerShape: Shape,
    innerShape: Shape,
    fill: Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(outerShape)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .semantics { contentDescription = description }
            .then(
                if (selected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, outerShape)
                } else Modifier
            )
            .padding(6.dp)
            .clip(innerShape)
            .then(fill),
    )
}

// Helpers

private fun String.humanize(): String =
    replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")
        .lowercase()
        .replaceFirstChar { it.uppercase() }