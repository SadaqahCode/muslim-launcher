package com.sadaqahcode.muslimlauncher.ui.drawer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.components.PinnedAppItem
import com.sadaqahcode.muslimlauncher.domain.model.DrawerModel
import my.nanihadesuka.compose.LazyVerticalGridScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
internal fun DrawerGridContent(
    model: DrawerModel,
    state: LazyGridState,
    nested: NestedScrollConnection,
    scrollbarSettings: ScrollbarSettings,
    topHeaderPadding: Dp,
    columns: Int,
    settings: UserSettings,
    onAppClick: (LauncherApp) -> Unit,
    onAppLongClick: (LauncherApp) -> Unit,
) {
    LazyVerticalGridScrollbar(
        state = state,
        settings = scrollbarSettings,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nested),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            model.sections.forEach { section ->
                val category = section.category
                if (category != null) {
                    item(
                        key = "header_${category.name}",
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        CategoryHeader(category = category, topPadding = topHeaderPadding)
                    }
                }
                items(
                    items = section.apps,
                    key = { "${it.packageName}|${it.userId}|${it.componentName}" },
                ) { app ->
                    PinnedAppItem(
                        app = app,
                        onClick = { onAppClick(app) },
                        onLongClick = { onAppLongClick(app) },
                        settings = settings,
                    )
                }
            }
        }
    }
}