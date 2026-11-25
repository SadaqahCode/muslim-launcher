package com.sadaqahcode.muslimlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AdaptiveSidebarLayout(
    isTablet: Boolean,
    permanentSidebar: Boolean,
    drawerOpen: Boolean,
    onDrawerClose: () -> Unit,
    onDrawerOpen: () -> Unit = {},
    sidebarWidth: Dp = 320.dp,
    sidebarWindowInsets: WindowInsets = WindowInsets(0, 0, 0, 0),
    contentWindowInsets: WindowInsets = WindowInsets(0, 0, 0, 0),
    sidebarContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isTablet && permanentSidebar) {
        Row(modifier.fillMaxSize()) {
            Box(
                Modifier
                    .width(sidebarWidth)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(sidebarWindowInsets)
                ) {
                    sidebarContent()
                }
            }
            Box(
                Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            )
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .windowInsetsPadding(contentWindowInsets)
            ) {
                content()
            }
        }
        return
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Caller → drawer
    LaunchedEffect(drawerOpen) {
        if (drawerOpen) drawerState.open() else drawerState.close()
    }

    // Drawer → caller  (handles BOTH manual swipe-open and swipe-close)
    LaunchedEffect(drawerState.currentValue) {
        when (drawerState.currentValue) {
            DrawerValue.Open -> if (!drawerOpen) onDrawerOpen()
            DrawerValue.Closed -> if (drawerOpen) onDrawerClose()
            else -> Unit
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                windowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier
                    .width(sidebarWidth)
                    .fillMaxHeight(),
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(sidebarWindowInsets)
                ) {
                    sidebarContent()
                }
            }
        },
        modifier = modifier,
        content = content,
    )
}