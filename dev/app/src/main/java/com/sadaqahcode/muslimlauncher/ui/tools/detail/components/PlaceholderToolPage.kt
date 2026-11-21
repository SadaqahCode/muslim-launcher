package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp 
import androidx.compose.foundation.layout.navigationBarsPadding
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.theme.Wallpaper
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.ToolPageCard
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.ToolPageDefaults
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.isWindowLandscape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaceholderToolPage(tool: Tool, onClose: () -> Unit, settings: UserSettings, scheme: ColorScheme) {
    val isLandscape = isWindowLandscape()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Box(Modifier.fillMaxSize()) {
        Wallpaper(settings = settings, modifier = Modifier.fillMaxSize())
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            topBar = {
                val title: @Composable () -> Unit = {
                    Text(
                        text = stringResource(tool.titleRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                val nav: @Composable () -> Unit = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                            contentDescription = "Back",
                        )
                    }
                }
                val appBarColors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = scheme.onBackground,
                    navigationIconContentColor = scheme.onBackground,
                )
                if (isLandscape) {
                    TopAppBar(title = title, navigationIcon = nav, colors = appBarColors)
                } else {
                    LargeTopAppBar(
                        title = title,
                        navigationIcon = nav,
                        scrollBehavior = scrollBehavior,
                        colors = appBarColors,
                    )
                }
            },
        ) { innerPadding ->
            PlaceholderContent(tool, innerPadding)
        }
    }
}

@Composable
private fun PlaceholderContent(tool: Tool, contentPadding: PaddingValues) {
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
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurface,
                )
                Text(
                    text = "This tool is not ready yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}