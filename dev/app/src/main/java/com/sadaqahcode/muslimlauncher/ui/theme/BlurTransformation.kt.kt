package com.sadaqahcode.muslimlauncher.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import coil3.request.ImageRequest
import coil3.size.Size
import coil3.transform.BlurTransformation

@Composable
fun rememberWallpaperImageRequest(
    uri: String?,
    blurEnabled: Boolean,
    blurRadius: Dp,
): ImageRequest? {
    val context = LocalContext.current
    val density = LocalDensity.current

    return remember(uri, blurEnabled, blurRadius) {
        if (uri == null) null else {
            // Same key for all pages = cache hit on 2nd page
            val key = "wallpaper|$uri|blur=${if (blurEnabled) blurRadius.value else 0f}"
            val blurPx = with(density) { blurRadius.toPx() }.coerceIn(0f, 25f)

            ImageRequest.Builder(context)
                .data(uri)
                .size(Size.ORIGINAL)
                .transformations(
                    if (blurEnabled && blurPx > 0f) {
                        listOf(BlurTransformation(radius = blurPx, sampling = 2f))
                    } else emptyList()
                )
                .memoryCacheKey(key)
                .diskCacheKey(key)
                .build()
        }
    }
}