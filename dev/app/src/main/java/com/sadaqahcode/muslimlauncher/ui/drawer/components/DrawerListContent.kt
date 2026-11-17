package com.sadaqahcode.muslimlauncher.ui.drawer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.sadaqahcode.muslimlauncher.domain.model.AppItemLayout
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.components.PinnedAppItem
import com.sadaqahcode.muslimlauncher.domain.model.DrawerModel
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
internal fun DrawerListContent(
    model: DrawerModel,
    state: LazyListState,
    nested: NestedScrollConnection,
    scrollbarSettings: ScrollbarSettings,
    topHeaderPadding: Dp,
    settings: UserSettings,
    onAppClick: (LauncherApp) -> Unit,
    onAppLongClick: (LauncherApp) -> Unit,
) {
    // A "list" with text BELOW is just a 1-column grid.
    val itemSettings = if (settings.itemLayout == AppItemLayout.TextBelow) {
        settings.copy(itemLayout = AppItemLayout.TextBeside)
    } else settings

    LazyColumnScrollbar(
        state = state,
        settings = scrollbarSettings,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nested),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            model.sections.forEach { section ->
                val category = section.category
                if (category != null) {
                    item(key = "header_${category.name}") {
                        CategoryHeader(category = category, topPadding = topHeaderPadding)
                    }
                }
                items(section.apps, key = { it.packageName }) { app ->
                    PinnedAppItem(
                        app = app,
                        onClick = { onAppClick(app) },
                        onLongClick = { onAppLongClick(app) },
                        settings = itemSettings,
                    )
                }
            }
        }
    }
}