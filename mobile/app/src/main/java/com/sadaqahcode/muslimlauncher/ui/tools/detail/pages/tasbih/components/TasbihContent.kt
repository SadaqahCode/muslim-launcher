package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.sadaqahcode.muslimlauncher.ui.common.layout.ScreenLayout
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.Actions
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.TasbihUiState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr

@Composable
fun TasbihContent(
    toolTitle: String,
    state: TasbihUiState,
    language: String,
    actions: Actions,
    onClose: () -> Unit,
    scheme: ColorScheme,
    screen: ScreenLayout,
    onboardingDone: Boolean = false,
) {
    when {
        state.items.isEmpty() && state.error != null -> MessagePane(
            title = toolTitle,
            message = state.error,
            language = language,
            onClose = onClose,
            onRetry = actions.onRetry,
            showSidebar = screen.isLargeLandscape,
            colors = scheme,
        )

        state.items.isEmpty() -> MessagePane(
            title = toolTitle,
            message = tr(language, "Dzikir tidak tersedia.", "No dhikr available."),
            language = language,
            onClose = onClose,
            onRetry = null,
            showSidebar = screen.isLargeLandscape,
            colors = scheme,
        )

        else -> TasbihBody(
            toolTitle,
            state,
            language,
            actions,
            onClose,
            colors = scheme,
            screen = screen,
            onboardingDone = onboardingDone
        )
    }
}
