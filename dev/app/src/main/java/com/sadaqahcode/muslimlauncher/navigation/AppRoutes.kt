package com.sadaqahcode.muslimlauncher.navigation

import com.sadaqahcode.muslimlauncher.domain.model.ToolId
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperTextMode

object AppRoutes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val LICENSES = "licenses"

    const val TOOL_DETAIL =
        "tool/{toolId}?dark={dark}&mode={mode}&halo={halo}&adapt={adapt}"

    fun toolDetail(
        toolId: ToolId,
        wallpaperIsDark: Boolean = false,
        wallpaperTextMode: WallpaperTextMode = WallpaperTextMode.Auto,
        needsHalo: Boolean = true,
        adaptToWallpaper: Boolean = true,
    ): String {
        return "tool/${toolId.key}" +
                "?dark=$wallpaperIsDark" +
                "&mode=${wallpaperTextMode.name}" +
                "&halo=$needsHalo" +
                "&adapt=$adaptToWallpaper"
    }
}