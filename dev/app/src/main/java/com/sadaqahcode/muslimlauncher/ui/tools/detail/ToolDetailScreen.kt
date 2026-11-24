package com.sadaqahcode.muslimlauncher.ui.tools.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.domain.model.ToolId
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperTextMode
import com.sadaqahcode.muslimlauncher.ui.common.layout.ScreenTopPadding
import com.sadaqahcode.muslimlauncher.ui.common.layout.rememberScreenLayout
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.ProvideLegibleColors
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.NotFoundPage
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.PlaceholderToolPage
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.TasbihPage

internal typealias ToolPageContent = @Composable (
    tool: Tool,
    onClose: () -> Unit,
    topTransparentModal: Dp,
) -> Unit

private val ToolPages: Map<ToolId, ToolPageContent> = mapOf(
    ToolId.Tasbih to { tool, onClose, topTransparentModal ->
        TasbihPage(tool, onClose, topTransparentModal)
    },
    // add more
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

    val screen = rememberScreenLayout()
    val topTransparentModal = screen.topTransparent
    val screenTopPadding = ScreenTopPadding.resolve(screen)
    val useCollapsingBar = !(screen.isLandscape && !screen.isLargeLandscape)

    ProvideLegibleColors(
        wallpaperIsDark = wallpaperIsDark,
        mode = wallpaperTextMode,
        needsHalo = needsHalo,
        adaptToWallpaper = adaptToWallpaper,
    ) {
        val lc = LocalLegibleColors.current
        val scheme = lc.scheme

        if (tool == null) {
            NotFoundPage(
                onClose = onClose,
                settings = settings,
                scheme = scheme,
                textScale = textScale,
                paddingTop = screenTopPadding,
                useCollapsingBar = useCollapsingBar,
                paddingTopEnabled = settings.paddingTopEnabled,
            )
            return@ProvideLegibleColors
        }
        val page = ToolPages[tool.id]
        if (page != null) page(tool, onClose, topTransparentModal)
        else PlaceholderToolPage(
            tool = tool,
            onClose = onClose,
            settings = settings,
            scheme = scheme,
            textScale = textScale,
            paddingTop = screenTopPadding,
            useCollapsingBar = useCollapsingBar,
            paddingTopEnabled = settings.paddingTopEnabled,
        )
    }
}