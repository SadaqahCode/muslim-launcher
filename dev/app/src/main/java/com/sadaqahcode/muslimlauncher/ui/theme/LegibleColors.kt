package com.sadaqahcode.muslimlauncher.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperTextMode


private const val HALO_BLUR_LIGHT_TEXT = 4f
private const val HALO_BLUR_DARK_TEXT = 3f

@Immutable
data class LegibleColors(
    val scheme: ColorScheme,
    val halo: Color,
    val haloBlurRadius: Float,
) {
    val onWallpaper: Color get() = scheme.onBackground
    val onWallpaperMuted: Color get() = scheme.onSurfaceVariant
    val accent: Color get() = scheme.primary
}

fun TextStyle.withHalo(
    halo: Color,
    blurRadius: Float = HALO_BLUR_LIGHT_TEXT,
): TextStyle = copy(
    shadow = Shadow(
        color = halo,
        offset = Offset.Zero,
        blurRadius = blurRadius,
    ),
)

@Composable
fun rememberLegibleColors(
    wallpaperIsDark: Boolean,
    adaptToWallpaper: Boolean = true,
    mode: WallpaperTextMode = WallpaperTextMode.Auto,
    needsHalo: Boolean = true,
): LegibleColors {
    val scheme = MaterialTheme.colorScheme
    return remember(wallpaperIsDark, scheme, adaptToWallpaper, mode, needsHalo) {
        val effectiveIsDark = when (mode) {
            WallpaperTextMode.Auto -> wallpaperIsDark
            WallpaperTextMode.Light -> true
            WallpaperTextMode.Dark -> false
        }
        val shouldAdapt = adaptToWallpaper || mode != WallpaperTextMode.Auto

        val adjusted = if (!shouldAdapt) {
            scheme
        } else {
            val themeIsDark = scheme.background.luminance() < 0.5f
            val agree = effectiveIsDark == themeIsDark
            if (agree) scheme else scheme.copy(
                onBackground = scheme.inverseOnSurface,
                onSurface = scheme.inverseOnSurface,
                onSurfaceVariant = scheme.inverseOnSurface.copy(alpha = 0.72f),
                primary = scheme.inversePrimary,
            )
        }

        LegibleColors(
            scheme = adjusted,
            halo = when {
                !needsHalo -> Color.Transparent
                effectiveIsDark -> Color.White.copy(alpha = 0.45f)
                else -> Color.Black.copy(alpha = 0.25f)
            },
            haloBlurRadius = when {
                !needsHalo -> 0f
                effectiveIsDark -> HALO_BLUR_LIGHT_TEXT
                else -> HALO_BLUR_DARK_TEXT
            },
        )
    }
}

@Composable
fun ProvideLegibleColors(
    wallpaperIsDark: Boolean,
    adaptToWallpaper: Boolean = true,
    mode: WallpaperTextMode = WallpaperTextMode.Auto,
    needsHalo: Boolean = true,
    content: @Composable () -> Unit,
) {
    val lc = rememberLegibleColors(wallpaperIsDark, adaptToWallpaper, mode, needsHalo)
    CompositionLocalProvider(LocalLegibleColors provides lc, content = content)
}

val LocalLegibleColors = staticCompositionLocalOf<LegibleColors> {
    error(
        "LocalLegibleColors not provided. Wrap the content in " +
                "ProvideLegibleColors(wallpaperIsDark) { … } at the screen root."
    )
}