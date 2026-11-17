package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
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
internal fun HomeContentPortrait(
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

    Box(
        modifier.doubleTapGesture(
            onDoubleTap = onDoubleTapHome,
            isGatingOk = isGatingOk,
        )
    ) {
        Column(Modifier.fillMaxSize()) {
            LazyVerticalGridScrollbar(
                state = gridState,
                settings = softScrollbarSettings,
                modifier = Modifier.weight(1f),
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(settings.homeColumns),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = ContentGutter),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column {
                            Spacer(Modifier.height(48.dp))
                            HeroSection(state = heroState)
                            Spacer(Modifier.height(32.dp))
                        }
                    }

                    if (loading) {
                        // intentionally empty
                    } else if (homeApps.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EmptyPinnedHint()
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

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            if (dockApps.isNotEmpty()) {
                DockRowPortrait(
                    apps = dockApps,
                    slots = settings.dockSlots,
                    settings = settings,
                    onLaunch = onLaunch,
                    onAppLongClick = onAppLongClick,
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 4.dp, end = 8.dp)
                .size(38.dp)
                .background(
                    color = lc.scheme.surfaceContainer.copy(alpha = 0.8f),
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
                    tint = lc.scheme.onBackground.copy(alpha = 0.6f),
                )
            }
        }
    }
}