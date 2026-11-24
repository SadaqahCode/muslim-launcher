package com.sadaqahcode.muslimlauncher.ui.common.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarPage(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: (@Composable () -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    useCollapsingBar: Boolean = true,
    paddingTop: Dp = 0.dp,
    paddingTopEnabled: Boolean = true,
    wrapperModifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = WindowInsets(0, 0, 0, 0),
    content: @Composable (PaddingValues) -> Unit,
) {
    val lc = LocalLegibleColors.current
    val canCollapse = useCollapsingBar && paddingTopEnabled
    val effectivePaddingTop = if (paddingTopEnabled) paddingTop else 0.dp

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val expandedHeight = TopAppBarDefaults.LargeAppBarExpandedHeight + effectivePaddingTop

    val scaffoldModifier = modifier
        .widthIn(max = PageMaxWidthScreen)
        .fillMaxSize()
        .then(
            if (canCollapse) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            else Modifier
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(wrapperModifier),
        contentAlignment = Alignment.TopCenter,
    ) {
        Scaffold(
            modifier = scaffoldModifier,
            containerColor = Color.Transparent,
            contentWindowInsets = contentWindowInsets,
            topBar = {
                val collapsed by remember(scrollBehavior) {
                    derivedStateOf { scrollBehavior.state.collapsedFraction > 0.5f }
                }

                val computedTitle: @Composable () -> Unit = {
                    if (!canCollapse || subtitle == null) {
                        title()
                    } else {
                        Column {
                            title()
                            if (!collapsed) subtitle()
                        }
                    }
                }

                val appBarColors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = lc.scheme.onBackground,
                    navigationIconContentColor = lc.scheme.onBackground,
                    actionIconContentColor = lc.scheme.onBackground,
                )

                if (canCollapse) {
                    LargeTopAppBar(
                        title = computedTitle,
                        navigationIcon = navigationIcon ?: {},
                        actions = actions,
                        expandedHeight = expandedHeight,
                        scrollBehavior = scrollBehavior,
                        colors = appBarColors,
                    )
                } else {
                    TopAppBar(
                        title = computedTitle,
                        navigationIcon = navigationIcon ?: {},
                        actions = actions,
                        colors = appBarColors,
                    )
                }
            },
            content = content,
        )
    }
}

@Composable
fun AppBarPage(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    textScale: AppTextSize? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    useCollapsingBar: Boolean = true,
    paddingTop: Dp = 0.dp,
    paddingTopEnabled: Boolean = true,
    wrapperModifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = WindowInsets(0, 0, 0, 0),
    content: @Composable (PaddingValues) -> Unit,
) {
    val lc = LocalLegibleColors.current
    val titleStyle = textScale?.scaled(MaterialTheme.typography.titleLarge)
        ?: MaterialTheme.typography.titleLarge
    val subtitleStyle = textScale?.scaled(MaterialTheme.typography.bodySmall)
        ?: MaterialTheme.typography.bodySmall

    AppBarPage(
        title = {
            Text(
                text = title,
                style = titleStyle,
                fontWeight = FontWeight.Medium,
                color = lc.scheme.onBackground,
            )
        },
        subtitle = subtitle?.let {
            {
                Text(
                    text = it,
                    style = subtitleStyle,
                    color = lc.scheme.onBackground.copy(alpha = 0.72f),
                )
            }
        },
        navigationIcon = onBack?.let {
            {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                        contentDescription = "Back",
                        tint = lc.scheme.onBackground,
                    )
                }
            }
        },
        actions = actions,
        modifier = modifier,
        useCollapsingBar = useCollapsingBar,
        paddingTop = paddingTop,
        paddingTopEnabled = paddingTopEnabled,
        wrapperModifier = wrapperModifier,
        contentWindowInsets = contentWindowInsets,
        content = content,
    )
}