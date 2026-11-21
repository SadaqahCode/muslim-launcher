package com.sadaqahcode.muslimlauncher.ui.tools.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.theme.Wallpaper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotFoundPage(
    onClose: () -> Unit,
    settings: UserSettings,
    scheme: ColorScheme,
    textScale: AppTextSize,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val appBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
        titleContentColor = scheme.onBackground,
        navigationIconContentColor = scheme.onBackground,
    )

    Box(Modifier.fillMaxSize()) {
        Wallpaper(settings = settings, modifier = Modifier.fillMaxSize())
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            topBar = {
                ToolTopAppBar(
                    title = "Tool not found",
                    onClose = onClose,
                    scrollBehavior = scrollBehavior,
                    appBarColors = appBarColors
                )
            },
        ) { innerPadding ->
            NotFoundContent(innerPadding, scheme = scheme, textScale = textScale)
        }
    }
}

@Composable
private fun NotFoundContent(
    contentPadding: PaddingValues,
    scheme: ColorScheme,
    textScale: AppTextSize,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(ToolPageDefaults.contentPadding(contentPadding)),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        ToolPageCard {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "This tool is not available.",
                    style = textScale.scaled(MaterialTheme.typography.bodyLarge),
                    color = scheme.onSurface,
                )
                Text(
                    text = "It may have been removed or renamed in this version.",
                    style = textScale.scaled(MaterialTheme.typography.bodySmall),
                    color = scheme.onSurfaceVariant,
                )
            }
        }
    }
}