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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Size
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperMode
import androidx.core.graphics.toColorInt

@Composable
fun Wallpaper(
    settings: UserSettings,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uri = settings.wallpaperImageUri
    val blurEnabled = settings.wallpaperBlurEnabled

    // Blurred = 360px, Not blurred = max 1080x1920, never 10 MB full
    val decodeSize = if (blurEnabled) Size(360, 360) else Size(1080, 1920)

    Box(modifier = modifier.fillMaxSize()) {
        when (settings.wallpaperMode) {
            WallpaperMode.System -> {}
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
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xFF475569))
                ) {
                    if (uri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(uri)
                                .size(decodeSize)
                                .build(),
                            contentDescription = "Custom wallpaper",
                            modifier = Modifier
                                .fillMaxSize()
                                .then(if (blurEnabled) Modifier.blur(settings.wallpaperBlurRadius.dp) else Modifier),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
        }
    }
}

private fun parseHexColor(hex: String, fallback: Color): Color =
    runCatching { Color(hex.toColorInt()) }.getOrDefault(fallback)