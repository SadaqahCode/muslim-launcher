package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
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
internal fun DockRowHorizontal(
    apps: List<LauncherApp>,
    slots: Int,
    settings: UserSettings,
    onLaunch: (LauncherApp) -> Unit,
    onAppLongClick: (LauncherApp) -> Unit,
) {
    val scrollable = apps.size > slots

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (scrollable) {
            Arrangement.spacedBy(20.dp)
        } else {
            Arrangement.SpaceEvenly
        },
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = DockGutter, vertical = 8.dp),
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