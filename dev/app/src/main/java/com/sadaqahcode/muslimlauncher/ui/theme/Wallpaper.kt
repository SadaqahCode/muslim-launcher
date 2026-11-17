package com.sadaqahcode.muslimlauncher.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperMode
import androidx.core.graphics.toColorInt

@Composable
fun Wallpaper(
    settings: UserSettings,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (settings.wallpaperMode) {
            WallpaperMode.System -> {
                // Intentionally empty. The transparent Scaffold lets the
                // real system wallpaper show through from the window below.
            }

            WallpaperMode.Color -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(parseHexColor(settings.wallpaperColor, Color.White))
                )
            }

            WallpaperMode.Gradient -> {
                val start = parseHexColor(settings.wallpaperGradientStart, Color.Blue)
                val end = parseHexColor(settings.wallpaperGradientEnd, Color.Magenta)
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(start, end)))
                )
            }

            WallpaperMode.Image -> {
                val uri = settings.wallpaperImageUri
                if (uri != null) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Custom wallpaper",
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (settings.wallpaperBlurEnabled) {
                                    // No-op on API < 31 — graceful degradation.
                                    Modifier.blur(settings.wallpaperBlurRadius.dp)
                                } else Modifier
                            ),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    // URI lost (e.g. user revoked permission) — fall back.
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color(0xFF475569))
                    )
                }
            }
        }
    }
}

/** Parse `#AARRGGBB` or `#RRGGBB` into Compose Color, with fallback. */
private fun parseHexColor(hex: String, fallback: Color): Color =
    runCatching { Color(hex.toColorInt()) }.getOrDefault(fallback)