package com.sadaqahcode.muslimlauncher.ui.widgets.helper

import android.appwidget.AppWidgetProviderInfo
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.graphics.drawable.toBitmap
import com.sadaqahcode.muslimlauncher.data.launcher.shapeFor
import com.sadaqahcode.muslimlauncher.data.launcher.toShapedBitmap
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Longest edge we rasterise a widget preview at. */
private const val PREVIEW_MAX_DIM = 512

/**
 * Strip transparent padding some widget authors bake into preview PNGs.
 * Off by default — only turn on if your widget set actually ships padded previews.
 */
private const val TRIM_PREVIEW_PADDING = false

/** Android stores widget min size as `70 * cells - 30` dp; invert it. */
private fun dpToCells(dp: Int): Int =
    if (dp <= 0) 1 else ((dp + 30) / 70).coerceAtLeast(1)

/**
 * Cell footprint aspect (w/h). Fallback used when a provider ships no preview
 * bitmap, or when the preview drawable reports no intrinsic size.
 */
internal fun widgetCellAspectRatio(info: AppWidgetProviderInfo): Float {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (info.targetCellWidth > 0 && info.targetCellHeight > 0) {
            return info.targetCellWidth.toFloat() / info.targetCellHeight.toFloat()
        }
    }
    return dpToCells(info.minWidth).toFloat() / dpToCells(info.minHeight).toFloat()
}

private fun Bitmap.trimTransparent(): Bitmap {
    val w = width
    val h = height
    if (w <= 0 || h <= 0) return this
    val px = IntArray(w * h).also { getPixels(it, 0, w, 0, 0, w, h) }

    fun rowOpaque(y: Int) = (0 until w).any { px[y * w + it] ushr 24 != 0 }
    fun colOpaque(x: Int) = (0 until h).any { px[it * w + x] ushr 24 != 0 }

    var top = 0
    var bottom = h - 1
    var left = 0
    var right = w - 1
    while (top < bottom && !rowOpaque(top)) top++
    while (bottom > top && !rowOpaque(bottom)) bottom--
    while (left < right && !colOpaque(left)) left++
    while (right > left && !colOpaque(right)) right--

    if (top == 0 && left == 0 && right == w - 1 && bottom == h - 1) return this
    return Bitmap.createBitmap(this, left, top, right - left + 1, bottom - top + 1)
}

@Composable
internal fun rememberWidgetPreview(info: AppWidgetProviderInfo): ImageBitmap? {
    val context = LocalContext.current
    val densityDpi = LocalConfiguration.current.densityDpi
    val key = info.provider.flattenToString()

    return produceState<ImageBitmap?>(initialValue = null, key, densityDpi) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val drawable = info.loadPreviewImage(context, densityDpi)
                    ?: return@runCatching null

                val w = drawable.intrinsicWidth
                val h = drawable.intrinsicHeight
                val (targetW, targetH) = if (w > 0 && h > 0) {
                    val scale = minOf(1f, PREVIEW_MAX_DIM.toFloat() / maxOf(w, h))
                    (w * scale).toInt().coerceAtLeast(1) to
                            (h * scale).toInt().coerceAtLeast(1)
                } else {
                    val aspect = widgetCellAspectRatio(info)
                    val tw = PREVIEW_MAX_DIM
                    val th = (tw / aspect).toInt().coerceIn(1, PREVIEW_MAX_DIM * 4)
                    tw to th
                }

                val bmp = drawable.toBitmap(targetW, targetH)
                val finalBmp = if (TRIM_PREVIEW_PADDING) bmp.trimTransparent() else bmp
                finalBmp.asImageBitmap()
            }.getOrNull()
        }
    }.value
}

/**
 * Loads and shapes an app icon on IO. Async via [produceState] — returns `null`
 * for the first frame(s), then the loaded bitmap.
 *
 * The synchronous version this replaces was blocking the picker's first frame
 * by ~17 ms per group (binder call to PackageManager + adaptive icon
 * rasterisation). N groups → N × 17 ms main-thread work.
 */
@Composable
internal fun rememberAppIcon(
    packageName: String,
    settings: UserSettings,
): ImageBitmap? {
    val context = LocalContext.current
    val density = LocalDensity.current

    return produceState<ImageBitmap?>(
        initialValue = null,
        packageName,
        settings.iconShape,
        settings.iconSize,
    ) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val drawable = context.packageManager.getApplicationIcon(packageName)
                val sizePx = with(density) { settings.iconSize.dp.roundToPx() }
                drawable.toShapedBitmap(
                    sizePx = sizePx,
                    shape = shapeFor(settings.iconShape),
                    density = density,
                )
            }.getOrNull()
        }
    }.value
}

/**
 * Loads and shapes a shortcut icon on IO. Async via [produceState] — returns
 * `null` until the bitmap is ready. Same reasoning as [rememberAppIcon]:
 * `ResolveInfo.loadIcon` is a binder call plus a decode, unsafe for main thread
 * when N icons render in a list.
 */
@Composable
internal fun rememberShortcutIcon(
    info: ResolveInfo,
    settings: UserSettings,
): ImageBitmap? {
    val context = LocalContext.current
    val density = LocalDensity.current
    val key = "${info.activityInfo.packageName}/${info.activityInfo.name}"

    return produceState<ImageBitmap?>(
        initialValue = null,
        key,
        settings.iconShape,
        settings.iconSize,
    ) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val drawable = info.loadIcon(context.packageManager)
                val sizePx = with(density) { settings.iconSize.dp.roundToPx() }
                drawable.toShapedBitmap(
                    sizePx = sizePx,
                    shape = shapeFor(settings.iconShape),
                    density = density,
                )
            }.getOrNull()
        }
    }.value
}