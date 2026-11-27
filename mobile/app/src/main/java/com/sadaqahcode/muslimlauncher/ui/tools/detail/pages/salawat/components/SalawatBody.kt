package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.salawat.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.common.layout.PageMaxWidthLargeScreen
import com.sadaqahcode.muslimlauncher.ui.common.layout.PageMaxWidthScreen
import com.sadaqahcode.muslimlauncher.ui.common.layout.ScreenLayout
import com.sadaqahcode.muslimlauncher.ui.components.AdaptiveSidebarLayout
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.salawat.SalawatActions
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.salawat.SalawatUiState

@Composable
fun SalawatBody(
    toolTitle: String,
    state: SalawatUiState,
    language: String,
    actions: SalawatActions,
    onClose: () -> Unit,
    screen: ScreenLayout,
    lcColors: ColorScheme,
    defaultColors: ColorScheme,
) {
    var showList by remember { mutableStateOf(false) }

    val isTablet = screen.isTablet
    val isSplit = screen.isSplit
    val isLargeLandscape = screen.isLargeLandscape
    val permanentSidebar = isTablet && isLargeLandscape
    val contentMaxWidth =
        if (isLargeLandscape) PageMaxWidthLargeScreen else PageMaxWidthScreen

    val onPick: (Int) -> Unit = { id ->
        actions.onSelect(id)
        if (!permanentSidebar) showList = false
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.widthIn(max = contentMaxWidth).fillMaxSize()) {
            AdaptiveSidebarLayout(
                isTablet = isTablet,
                permanentSidebar = permanentSidebar,
                drawerOpen = showList,
                onDrawerClose = { showList = false },
                onDrawerOpen = { showList = true },
                sidebarWidth = 320.dp,
                sidebarWindowInsets = WindowInsets.systemBars,
                contentWindowInsets = WindowInsets(0),
                sidebarContent = {
                    SalawatListContent(
                        state = state,
                        language = language,
                        onPick = onPick,
                        onToggleFavorite = actions.onToggleFavorite,
                        onToggleFavoritesOnly = actions.onToggleFavoritesOnly,
                        modifier = Modifier.fillMaxSize(),
                        lcColors = lcColors,
                        defaultColors = defaultColors,
                    )
                },
                content = {
                    SalawatDetailPane(
                        toolTitle = toolTitle,
                        state = state,
                        language = language,
                        showSidebar = permanentSidebar,
                        onToggleFavorite = actions.onToggleFavorite,
                        onRefresh = actions.onRefresh,
                        onOpenList = if (!permanentSidebar) ({ showList = true }) else null,
                        onClose = onClose,
                        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
                        lcColors = lcColors,
                        defaultColors = defaultColors,
                        split = isTablet || isSplit,
                    )
                },
            )
        }
    }
}