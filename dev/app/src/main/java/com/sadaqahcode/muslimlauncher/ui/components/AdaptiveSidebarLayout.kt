package com.sadaqahcode.muslimlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
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
    drawerOpen: Boolean,
    onDrawerClose: () -> Unit,
    sidebarWidth: Dp = 320.dp,
    sidebarContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isTablet) {
        // Tablet: permanent sidebar
        Row(modifier.fillMaxSize()) {
            Box(
                Modifier
                    .width(sidebarWidth)
                    .fillMaxHeight()
            ) {
                sidebarContent()
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
            ) {
                content()
            }
        }
        return
    }

    // Phone / small: side drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(drawerOpen) {
        if (drawerOpen) drawerState.open() else drawerState.close()
    }
    LaunchedEffect(drawerState.isClosed) {
        if (drawerState.isClosed && drawerOpen) onDrawerClose()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.width(sidebarWidth),
            ) {
                sidebarContent()
            }
        },
        modifier = modifier,
        content = content,
    )
}