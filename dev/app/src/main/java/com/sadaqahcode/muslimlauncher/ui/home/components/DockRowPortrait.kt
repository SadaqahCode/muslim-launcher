package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.components.DockIcon


@Composable
internal fun DockRowVertical(
    apps: List<LauncherApp>,
    slots: Int,
    settings: UserSettings,
    onLaunch: (LauncherApp) -> Unit,
    onAppLongClick: (LauncherApp) -> Unit,
) {
    val scrollable = apps.size > slots
    val itemCount = if (scrollable) apps.size else slots

    LazyColumn(
        modifier = Modifier
            .width(72.dp)
            .fillMaxHeight()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically,
        ),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        if (scrollable) {
            items(apps, key = { it.packageName }) { app ->
                DockIcon(
                    app = app,
                    onClick = { onLaunch(app) },
                    onLongClick = { onAppLongClick(app) },
                    settings = settings,
                )
            }
        } else {
            itemsIndexed(
                items = List(slots) { apps.getOrNull(it) },
                key = { index, app -> app?.packageName ?: "slot_$index" },
            ) { _, app ->
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