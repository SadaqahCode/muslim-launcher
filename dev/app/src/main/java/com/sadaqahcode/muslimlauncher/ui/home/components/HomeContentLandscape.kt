package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.components.PinnedAppItem
import com.sadaqahcode.muslimlauncher.ui.home.PrayerHeroState
import com.sadaqahcode.muslimlauncher.ui.home.helper.doubleTapGesture
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import my.nanihadesuka.compose.LazyVerticalGridScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
internal fun HomeContentLandscape(
    modifier: Modifier,
    heroState: PrayerHeroState,
    homeApps: List<LauncherApp>,
    dockApps: List<LauncherApp>,
    loading: Boolean,
    settings: UserSettings,
    onOpenSettings: () -> Unit,
    onLaunch: (LauncherApp) -> Unit,
    onAppLongClick: (LauncherApp) -> Unit,
    onDoubleTapHome: () -> Unit,
    isGatingOk: () -> Boolean = { true },
    onOpenDrawer: () -> Unit,
) {
    val gridState = rememberLazyGridState()
    val lc = LocalLegibleColors.current

    val softScrollbarSettings = ScrollbarSettings.Default.copy(
        thumbThickness = 5.dp,
        thumbUnselectedColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f),
        thumbSelectedColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
        scrollbarPadding = 0.dp,
    )

    Row(
        modifier = modifier
            .doubleTapGesture(onDoubleTap = onDoubleTapHome, isGatingOk = isGatingOk)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ContentGutter, vertical = 12.dp),
        ) {
            Box(Modifier.fillMaxWidth()) {
                HeroSection(
                    state = heroState,
                    textScale = settings.textSize
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 8.dp)
                        .size(38.dp)
                        .background(
                            color = lc.scheme.surfaceContainer.copy(alpha = 0.05f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(
                        onClick = onOpenSettings,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = "Theme settings",
                            tint = lc.scheme.onBackground.copy(alpha = 0.75f),
                        )
                    }
                }
            }
        }

        LazyVerticalGridScrollbar(
            state = gridState,
            settings = softScrollbarSettings,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(settings.homeColumns),
                state = gridState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (loading) {
                    // intentionally empty
                } else if (homeApps.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EmptyPinnedHint(textScale = settings.textSize,)
                    }
                } else {
                    items(homeApps, key = { it.packageName }) { app ->
                        PinnedAppItem(
                            app = app,
                            onClick = { onLaunch(app) },
                            onLongClick = { onAppLongClick(app) },
                            settings = settings,
                        )
                    }
                }
            }
        }

        if (dockApps.isNotEmpty()) {
            DockRowLandscape(
                apps = dockApps,
                slots = settings.dockSlots,
                settings = settings,
                onLaunch = onLaunch,
                onAppLongClick = onAppLongClick,
            )
        }
    }
}