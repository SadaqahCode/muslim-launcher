package com.sadaqahcode.muslimlauncher.data.wallpaper

import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import com.flagodna.debugprint.debugPrint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SystemWallpaperState {
    private const val TAG = "SystemWallpaperState"
    private const val DARK_LUMINANCE_THRESHOLD = 0.5f

    private val _isDark = MutableStateFlow(false)
    val isDark: StateFlow<Boolean> = _isDark.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    internal fun update(colors: WallpaperColors?) {
        if (colors == null) {
            debugPrint.d("$TAG: null colors, keeping current isDark=${_isDark.value}")
            return
        }
        val luminance = ColorUtils.calculateLuminance(colors.primaryColor.toArgb())
        val dark = luminance < DARK_LUMINANCE_THRESHOLD
        _isDark.value = dark
        debugPrint.d("$TAG: updated isDark=$dark (luminance=$luminance)")
    }

    fun tryInit(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return
        @RequiresApi(Build.VERSION_CODES.O_MR1)
        fun probe() {
            runCatching {
                val wm = WallpaperManager.getInstance(context)
                update(wm.getWallpaperColors(WallpaperManager.FLAG_SYSTEM))
            }.onFailure {
                debugPrint.d("$TAG: init probe unavailable: ${it.message}")
            }
        }
        probe()
    }
}