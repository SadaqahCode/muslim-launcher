package com.sadaqahcode.muslimlauncher.ui.tools.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.common.layout.AppBarPage
import com.sadaqahcode.muslimlauncher.ui.theme.Wallpaper

@Composable
internal fun PlaceholderToolPage(
    tool: Tool,
    onClose: () -> Unit,
    settings: UserSettings,
    scheme: ColorScheme,
    textScale: AppTextSize,
    paddingTop: Dp,
    useCollapsingBar: Boolean,
    paddingTopEnabled: Boolean,
) {
    Box(Modifier.fillMaxSize()) {
        Wallpaper(settings = settings, modifier = Modifier.fillMaxSize())

        AppBarPage(
            title = stringResource(tool.titleRes),
            textScale = textScale,
            onBack = onClose,
            useCollapsingBar = useCollapsingBar,
            paddingTop = paddingTop,
            paddingTopEnabled = paddingTopEnabled,
        ) { innerPadding ->
            PlaceholderContent(tool, innerPadding, textScale)
        }
    }
}

@Composable
private fun PlaceholderContent(
    tool: Tool,
    contentPadding: PaddingValues,
    textScale: AppTextSize,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(ToolPageDefaults.contentPadding(contentPadding))
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        ToolPageCard {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(tool.subtitleRes),
                    style = textScale.scaled(MaterialTheme.typography.bodyLarge),
                    color = colors.onSurface,
                )
                Text(
                    text = "This tool is not ready yet.",
                    style = textScale.scaled(MaterialTheme.typography.bodySmall),
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}