package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
internal fun DockRowPortrait(
    apps: List<LauncherApp>,
    slots: Int,
    settings: UserSettings,
    onLaunch: (LauncherApp) -> Unit,
    onAppLongClick: (LauncherApp) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DockGutter, vertical = 8.dp)
    ) {
        val itemWidth = maxWidth / slots

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(slots) { i ->
                val app = apps.getOrNull(i)
                Box(
                    modifier = Modifier.width(itemWidth),
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