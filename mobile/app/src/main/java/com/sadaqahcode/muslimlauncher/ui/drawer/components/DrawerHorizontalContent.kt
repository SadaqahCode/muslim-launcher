package com.sadaqahcode.muslimlauncher.ui.drawer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.components.PinnedAppItem
import com.sadaqahcode.muslimlauncher.domain.model.DrawerModel
import com.sadaqahcode.muslimlauncher.ui.drawer.DrawerSheetState

@Composable
internal fun DrawerHorizontalContent(
    model: DrawerModel,
    columns: Int,
    settings: UserSettings,
    sheet: DrawerSheetState,
    onAppClick: (LauncherApp) -> Unit,
    onAppLongClick: (LauncherApp) -> Unit,
) {
    val flatApps = remember(model) { model.sections.flatMap { it.apps } }

    val verticalDrag = rememberDraggableState { delta ->
        // delta > 0 = finger moving down = drawer should close (progress ↓)
        // delta < 0 = finger moving up   = drawer would open further
        sheet.dragBy(-delta)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .draggable(
                state = verticalDrag,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity -> sheet.settle(velocity) },
            )
    ) {
        val rowPitch = settings.iconSize.dp + 54.dp
        val rowSpacing = 12.dp
        val pagePaddingV = 8.dp
        val dotsReserve = 26.dp

        val available1 = maxHeight - pagePaddingV * 2
        val rowsNoDots = (((available1 + rowSpacing) / rowPitch)
            .toInt()).coerceAtLeast(1)
        val itemsNoDots = columns * rowsNoDots
        val pagesNoDots = if (flatApps.isEmpty()) 1
        else (flatApps.size + itemsNoDots - 1) / itemsNoDots

        val willShowDots = pagesNoDots > 1
        val available2 = maxHeight - pagePaddingV * 2 - if (willShowDots) dotsReserve else 0.dp
        val rowsPerPage = (((available2 + rowSpacing) / rowPitch)
            .toInt()).coerceAtLeast(1)

        val itemsPerPage = columns * rowsPerPage

        val pages = remember(flatApps, itemsPerPage) {
            if (flatApps.isEmpty()) listOf(emptyList())
            else flatApps.chunked(itemsPerPage)
        }
        val pagerState = rememberPagerState(pageCount = { pages.size })

        Column(Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                pageSpacing = 16.dp,
            ) { pageIndex ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = pagePaddingV),
                    verticalArrangement = Arrangement.spacedBy(rowSpacing),
                ) {
                    pages[pageIndex].chunked(columns).forEach { rowApps ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            rowApps.forEach { app ->
                                Box(modifier = Modifier.weight(1f)) {
                                    PinnedAppItem(
                                        app = app,
                                        onClick = { onAppClick(app) },
                                        onLongClick = { onAppLongClick(app) },
                                        settings = settings,
                                    )
                                }
                            }
                            repeat(columns - rowApps.size) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            if (pages.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(pages.size) { index ->
                        val active = index == pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (active) 6.dp else 4.dp)
                                .background(
                                    color = if (active) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    },
                                    shape = CircleShape,
                                )
                        )
                    }
                }
            }
        }
    }
}