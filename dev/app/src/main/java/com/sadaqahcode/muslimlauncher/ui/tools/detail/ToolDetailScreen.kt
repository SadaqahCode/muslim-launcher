package com.sadaqahcode.muslimlauncher.ui.tools.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.domain.model.ToolId
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperTextMode
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.ProvideLegibleColors
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.NotFoundPage
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.PlaceholderToolPage
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.TasbihPage

internal typealias ToolPageContent = @Composable (tool: Tool, onClose: () -> Unit) -> Unit

private val ToolPages: Map<ToolId, ToolPageContent> = mapOf(
    ToolId.Tasbih to { tool, onClose -> TasbihPage(tool, onClose) },
)

@Composable
fun ToolDetailScreen(
    tool: Tool?,
    onClose: () -> Unit,
    wallpaperIsDark: Boolean = false,
    wallpaperTextMode: WallpaperTextMode = WallpaperTextMode.Auto,
    needsHalo: Boolean = true,
    adaptToWallpaper: Boolean = true,
    detailVm: ToolDetailViewModel = hiltViewModel(),
) {
    val settings by detailVm.settings.collectAsStateWithLifecycle()
    val textScale = settings.textSize

    ProvideLegibleColors(
        wallpaperIsDark = wallpaperIsDark,
        mode = wallpaperTextMode,
        needsHalo = needsHalo,
        adaptToWallpaper = adaptToWallpaper,
    ) {
        val lc = LocalLegibleColors.current
        val scheme = lc.scheme

        if (tool == null) {
            NotFoundPage(onClose, settings = settings, scheme = scheme, textScale = textScale)
            return@ProvideLegibleColors
        }
        val page = ToolPages[tool.id]
        if (page != null) page(tool, onClose) else PlaceholderToolPage(tool, onClose, settings = settings, scheme = scheme, textScale = textScale)
    }
}