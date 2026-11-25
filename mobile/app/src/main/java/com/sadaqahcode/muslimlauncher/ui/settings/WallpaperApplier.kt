package com.sadaqahcode.muslimlauncher.ui.settings

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import com.flagodna.debugprint.debugPrint
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt

private const val TAG = "WallpaperApplier"

internal suspend fun applyToSystemWallpaper(
    context: Context,
    settings: UserSettings,
) {
    withContext(Dispatchers.IO) {
        val wm = WallpaperManager.getInstance(context)

        // Build the "apply" closure once; reuse it across the fallback chain.
        suspend fun tryApply(flags: Int, label: String): Boolean {
            return try {
                when (settings.wallpaperMode) {
                    WallpaperMode.System -> {
                        val color = runCatching {
                            settings.wallpaperColor.toColorInt()
                        }.getOrNull() ?: return false
                        val bmp = solidBitmap(color)
                        try {
                            setBitmapCompat(wm, bmp, flags)
                        } finally {
                            bmp.recycle()
                        }
                    }

                    WallpaperMode.Color -> {
                        val color = runCatching {
                            settings.wallpaperColor.toColorInt()
                        }.getOrNull() ?: return false
                        val bmp = solidBitmap(color)
                        try {
                            setBitmapCompat(wm, bmp, flags)
                        } finally {
                            bmp.recycle()
                        }
                    }

                    WallpaperMode.Gradient -> {
                        val start = runCatching {
                            settings.wallpaperGradientStart.toColorInt()
                        }.getOrNull() ?: return false
                        val end = runCatching {
                            settings.wallpaperGradientEnd.toColorInt()
                        }.getOrNull() ?: return false
                        val bmp = gradientBitmap(start, end)
                        try {
                            setBitmapCompat(wm, bmp, flags)
                        } finally {
                            bmp.recycle()
                        }
                    }

                    WallpaperMode.Image -> {
                        val uriString = settings.wallpaperImageUri ?: return false
                        val uri = Uri.parse(uriString)
                        val stream = context.contentResolver.openInputStream(uri)
                            ?: run {
                                debugPrint.e("$TAG: could not open stream for $uriString")
                                return false
                            }
                        stream.use {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                wm.setStream(it, null, true, flags)
                            } else {
                                @Suppress("DEPRECATION")
                                wm.setStream(it)
                            }
                        }
                    }
                }
                debugPrint.d("$TAG: ✓ applied to $label (flags=$flags)")
                true
            } catch (e: Exception) {
                debugPrint.w("$TAG: ✗ $label failed: ${e::class.simpleName}: ${e.message}")
                false
            }
        }

        // Fallback chain — stop at the first success so we don't rewrite the
        // same wallpaper redundantly, and so the log tells us which combination
        // the device actually accepted.
        val bothFlags = WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK

        when {
            tryApply(bothFlags, "home+lock") -> Unit
            tryApply(WallpaperManager.FLAG_SYSTEM, "home only") -> Unit
            tryApply(WallpaperManager.FLAG_LOCK, "lock only") -> Unit
            else -> debugPrint.e(
                "$TAG: all attempts failed — wallpaper not applied. " +
                        "This OEM may block third-party wallpaper writes entirely."
            )
        }
    }
}

private fun setBitmapCompat(
    wm: WallpaperManager,
    bitmap: Bitmap,
    flags: Int,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        wm.setBitmap(bitmap, null, true, flags)
    } else {
        @Suppress("DEPRECATION")
        wm.setBitmap(bitmap)
    }
}

/** 1080×1920 solid bitmap. The system scales/crops as needed. */
private fun solidBitmap(color: Int): Bitmap =
    createBitmap(1080, 1920).apply {
        eraseColor(color)
    }

/** 1080×1920 top-to-bottom gradient. */
private fun gradientBitmap(startColor: Int, endColor: Int): Bitmap {
    val w = 1080
    val h = 1920
    val bmp = createBitmap(w, h)
    val canvas = Canvas(bmp)
    val paint = Paint().apply {
        shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            startColor, endColor, Shader.TileMode.CLAMP,
        )
    }
    canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
    return bmp
}