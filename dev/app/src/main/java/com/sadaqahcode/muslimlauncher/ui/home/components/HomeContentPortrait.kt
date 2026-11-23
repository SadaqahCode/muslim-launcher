package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.components.PinnedAppItem
import com.sadaqahcode.muslimlauncher.ui.home.PrayerHeroState
import com.sadaqahcode.muslimlauncher.ui.home.helper.doubleTapGesture
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.glass

@Composable
internal fun HomeContentPortrait(
    modifier: Modifier,
    maxWidth: Dp,
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
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            Modifier
                .widthIn(max = maxWidth)
                .fillMaxHeight()
                .doubleTapGesture(
                    onDoubleTap = onDoubleTapHome,
                    isGatingOk = isGatingOk,
                )
        ) {
            Column(Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(settings.homeColumns),
                    state = gridState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = ContentGutter),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column {
                            Spacer(Modifier.height(48.dp))
                            HeroSection(
                                state = heroState,
                                textScale = settings.textSize
                            )
                            Spacer(Modifier.height(32.dp))
                        }
                    }

                    if (loading) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    color = colors.primary,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                        }
                    } else if (homeApps.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EmptyPinnedHint(textScale = settings.textSize,)
                        }
                    } else {
                        items(
                            items = homeApps,
                            key = { "${it.packageName}|${it.userId}|${it.componentName}" },
                        ) { app ->
                            PinnedAppItem(
                                app = app,
                                onClick = { onLaunch(app) },
                                onLongClick = { onAppLongClick(app) },
                                settings = settings,
                            )
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
                    .glass(RoundedCornerShape(20.dp))
                    .size(38.dp),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick = onOpenSettings,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = "Theme settings",
                        tint = colors.onBackground.copy(alpha = 0.65f),
                    )
                }
            }
        }
    }
}