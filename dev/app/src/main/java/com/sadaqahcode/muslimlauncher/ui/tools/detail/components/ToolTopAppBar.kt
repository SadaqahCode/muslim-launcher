package com.sadaqahcode.muslimlauncher.ui.tools.detail.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.sadaqahcode.muslimlauncher.ui.theme.glass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ToolTopAppBar(
    title: String,
    onClose: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    val isLandscape = isWindowLandscape()

    val titleContent: @Composable () -> Unit = {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    val navigationIcon: @Composable () -> Unit = {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                contentDescription = "Back",
            )
        }
    }
    val appBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = colors.background,
        scrolledContainerColor = colors.background,
        titleContentColor = colors.onBackground,
        navigationIconContentColor = colors.onBackground,
    )

    if (isLandscape) {
        TopAppBar(
            title = titleContent,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = appBarColors,
        )
    } else {
        LargeTopAppBar(
            title = titleContent,
            navigationIcon = navigationIcon,
            actions = actions,
            scrollBehavior = scrollBehavior,
            colors = appBarColors,
        )
    }
}