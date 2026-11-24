package com.sadaqahcode.muslimlauncher.domain.model

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppIconSize(val dp: Dp) {
    Small(36.dp),
    Medium(48.dp),
    Large(60.dp),
}

enum class AppTextSize(val sp: TextUnit) {
    Small(11.sp),
    Medium(13.sp),
    Large(15.sp),
    ExtraLarge(17.sp);

    val scale: Float
        get() = sp.value / Medium.sp.value

    fun scaled(base: TextUnit): TextUnit {
        return (base.value * scale).sp
    }

    fun scaled(style: TextStyle): TextStyle {
        return style.copy(fontSize = scaled(style.fontSize))
    }
}

enum class AppItemLayout {
    TextBelow,
    TextBeside,
    IconOnly,
}

enum class AppIconShape {
    Default,
    Box,
    Rounded,
    Circle,
}

enum class DrawerLayout {
    List,
    Grid,
    HorizontalScroll,
}

enum class DarkMode {
    Auto,
    Light,
    Dark,
}

enum class WallpaperMode {
    System,
    Color,
    Gradient,
    Image,
}

enum class ColorSource {
    LauncherDefault,
    AdaptivePhone,
    CustomColor,
}

enum class WallpaperTextMode {
    Auto,
    Light,
    Dark,
}


data class UserSettings(
    val language: AppLanguage = AppLanguage.English,
    val iconSize: AppIconSize = AppIconSize.Medium,
    val textSize: AppTextSize = AppTextSize.Medium,
    val itemLayout: AppItemLayout = AppItemLayout.TextBelow,
    val iconShape: AppIconShape = AppIconShape.Rounded,
    val drawerLayout: DrawerLayout = DrawerLayout.List,
    val homeColumns: Int = 2,
    val drawerColumns: Int = 4,
    val dockSlots: Int = 4,
    val maxPinned: Int = 8,
    val darkMode: DarkMode = DarkMode.Auto,
    val paddingTopEnabled: Boolean = false,
    val wallpaperMode: WallpaperMode = WallpaperMode.System,
    val wallpaperColor: String = "#FFFFFFFF",
    val wallpaperGradientStart: String = "#FF6200EE",
    val wallpaperGradientEnd: String = "#FF3700B3",
    val wallpaperImageUri: String? = null,
    val wallpaperBlurEnabled: Boolean = false,
    val wallpaperBlurRadius: Int = 20,
    val useDynamicColor: Boolean = true,
    val colorSource: ColorSource = ColorSource.LauncherDefault,
    val customSeedColor: String = "#FF2F5D50",
    val wallpaperTextMode: WallpaperTextMode = WallpaperTextMode.Auto,
)