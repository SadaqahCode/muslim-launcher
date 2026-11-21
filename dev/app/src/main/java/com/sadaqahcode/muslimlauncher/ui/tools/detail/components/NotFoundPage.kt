package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages

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
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.theme.LegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.Wallpaper
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.ToolPageCard
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.ToolPageDefaults
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.ToolTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotFoundPage(onClose: () -> Unit, settings: UserSettings, scheme: ColorScheme) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                )
            },
        ) { innerPadding ->
            NotFoundContent(innerPadding, scheme = scheme)
        }
    }
}

@Composable
private fun NotFoundContent(contentPadding: PaddingValues, scheme: ColorScheme) {
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
                    style = MaterialTheme.typography.bodyLarge,
                    color = scheme.onSurface,
                )
                Text(
                    text = "It may have been removed or renamed in this version.",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                )
            }
        }
    }
}