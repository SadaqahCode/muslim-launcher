package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.components.DockIcon

@Composable
internal fun DockRowLandscape(
    apps: List<LauncherApp>,
    slots: Int,
    settings: UserSettings,
    onLaunch: (LauncherApp) -> Unit,
    onAppLongClick: (LauncherApp) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .width(72.dp)
            .fillMaxHeight()
            .padding(horizontal = 8.dp),
    ) {
        val itemHeight = maxHeight / slots

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            repeat(slots) { i ->
                val app = apps.getOrNull(i)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    DockIcon(
                        app = app,
                        onClick = { app?.let { onLaunch(it) } },
                        onLongClick = { app?.let { onAppLongClick(it) } },
                        settings = settings,
                    )
                }
            }
        }
    }
}