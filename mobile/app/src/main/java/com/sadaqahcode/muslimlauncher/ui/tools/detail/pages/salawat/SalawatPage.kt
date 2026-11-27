package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.salawat

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.ui.common.layout.ScreenLayout
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.Wallpaper
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.salawat.components.SalawatBody
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr

class SalawatActions(
    val onSelect: (Int) -> Unit,
    val onToggleFavorite: (Int) -> Unit,
    val onToggleFavoritesOnly: () -> Unit,
    val onRefresh: () -> Unit,
)

@Composable
internal fun SalawatPage(
    tool: Tool,
    onClose: () -> Unit,
    screen: ScreenLayout,
    viewModel: SalawatViewModel = hiltViewModel(),
) {
    val lc = LocalLegibleColors.current
    val colors = lc.scheme
    val userSettings by viewModel.settings.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val language = LocalLocale.current.platformLocale.language

    val actions = remember(viewModel) {
        SalawatActions(
            onSelect = viewModel::select,
            onToggleFavorite = viewModel::toggleFavorite,
            onToggleFavoritesOnly = viewModel::toggleFavoritesOnly,
            onRefresh = viewModel::retry,
        )
    }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Box(Modifier.fillMaxSize()) {
        Wallpaper(settings = userSettings, modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { _ ->
            Box(Modifier.fillMaxSize()) {
                when {
                    state.isInitialLoading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator(color = colors.primary) }

                    state.error != null && state.items.isEmpty() -> SalawatError(
                        message = state.error ?: "",
                        language = language,
                        onRetry = viewModel::retry,
                    )

                    else -> {
                        SalawatBody(
                            toolTitle =  stringResource(tool.titleRes),
                            state = state,
                            language = language,
                            actions = actions,
                            onClose = onClose,
                            screen = screen,
                            colors = colors,
                        )
                        if (state.isRefreshing) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .fillMaxWidth()
                                    .height(2.dp),
                                color = colors.primary,
                                trackColor = colors.primary.copy(alpha = 0.15f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SalawatError(
    message: String,
    language: String,
    onRetry: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp),
            )
            TextButton(onClick = onRetry) {
                Text(tr(language, "Coba lagi", "Retry"))
            }
        }
    }
}