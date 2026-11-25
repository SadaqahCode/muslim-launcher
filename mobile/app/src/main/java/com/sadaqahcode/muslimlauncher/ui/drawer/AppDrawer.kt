package com.sadaqahcode.muslimlauncher.ui.drawer

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.AppCategory
import com.sadaqahcode.muslimlauncher.domain.model.AppItemLayout
import com.sadaqahcode.muslimlauncher.domain.model.DrawerLayout
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.domain.model.buildDrawerModel
import com.sadaqahcode.muslimlauncher.ui.common.layout.PageMaxWidthScreen
import com.sadaqahcode.muslimlauncher.ui.common.layout.rememberSoftScrollbarSettings
import com.sadaqahcode.muslimlauncher.ui.drawer.components.DrawerChipsRow
import com.sadaqahcode.muslimlauncher.ui.drawer.components.DrawerGridContent
import com.sadaqahcode.muslimlauncher.ui.drawer.components.DrawerHorizontalContent
import com.sadaqahcode.muslimlauncher.ui.drawer.components.DrawerListContent
import com.sadaqahcode.muslimlauncher.ui.drawer.components.DrawerSearchField
import com.sadaqahcode.muslimlauncher.ui.drawer.components.SheetNestedScroll
import com.sadaqahcode.muslimlauncher.ui.theme.ProvideLegibleColors

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun AppDrawer(
    apps: List<LauncherApp>,
    query: String,
    onQueryChange: (String) -> Unit,
    onAppClick: (LauncherApp) -> Unit,
    onAppLongClick: (LauncherApp) -> Unit,
    onClose: () -> Unit,
    sheet: DrawerSheetState,
    settings: UserSettings,
    hasWorkProfile: Boolean = false,
    isWorkPaused: Boolean = false,
    workApps: List<LauncherApp> = emptyList(),
    personalApps: List<LauncherApp> = emptyList(),
    topTransparent: Dp,
    onToggleWorkPaused: () -> Unit = {},
) {
    val themeIsDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    var filter by remember { mutableStateOf<AppCategory?>(null) }

    var showWork by remember { mutableStateOf(false) }
    LaunchedEffect(hasWorkProfile) {
        if (!hasWorkProfile) showWork = false
    }

    val displayedApps = remember(apps, personalApps, workApps, hasWorkProfile, showWork, query) {
        when {
            !hasWorkProfile -> apps
            query.isNotBlank() -> apps
            showWork -> workApps
            else -> personalApps
        }
    }
    val model = remember(displayedApps, query, filter) { buildDrawerModel(displayedApps, query, filter) }

    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val headerDrag = rememberDraggableState { delta -> sheet.dragBy(-delta) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val useHorizontalHeader = isLandscape || configuration.screenWidthDp.dp >= 600.dp

    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val isImeVisible = imeBottom > 0
    val isSearching = query.isNotBlank()

    val hideChrome = isImeVisible || isSearching

    val softScrollbar = rememberSoftScrollbarSettings()

    val onAppClickSafe: (LauncherApp) -> Unit = { app ->
        if (hasWorkProfile && showWork && isWorkPaused) {
            // Blocked until resumed
        } else {
            onAppClick(app)
        }
    }

    ProvideLegibleColors(
        wallpaperIsDark = themeIsDark,
        needsHalo = false,
        adaptToWallpaper = false,
    ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topTransparent),
                contentAlignment = Alignment.TopCenter,
            ) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 28.dp,
                        topEnd = 28.dp,
                    ),
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Column(
                        Modifier
                            .widthIn(max = PageMaxWidthScreen)
                            .fillMaxSize()
                            .systemBarsPadding()
                            .imePadding()
                    ) {
                        Column(
                            Modifier.then(
                                if (!hideChrome) Modifier.draggable(
                                    state = headerDrag,
                                    orientation = Orientation.Vertical,
                                    onDragStopped = { velocity -> sheet.settle(velocity) },
                                ) else Modifier
                            )
                        ) {
                            AnimatedVisibility(visible = !hideChrome) {
                                Column {
                                    if (hasWorkProfile) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(50))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                                    .padding(4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(50))
                                                        .background(
                                                            if (!showWork) MaterialTheme.colorScheme.primary
                                                            else Color.Transparent
                                                        )
                                                        .clickable { showWork = false }
                                                        .padding(vertical = 8.dp),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = "Personal",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = if (!showWork) FontWeight.SemiBold else FontWeight.Normal,
                                                        color = if (!showWork) MaterialTheme.colorScheme.onPrimary
                                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    )
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(50))
                                                        .background(
                                                            if (showWork) MaterialTheme.colorScheme.primary
                                                            else Color.Transparent
                                                        )
                                                        .clickable { showWork = true }
                                                        .padding(vertical = 8.dp),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = "Work",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = if (showWork) FontWeight.SemiBold else FontWeight.Normal,
                                                        color = if (showWork) MaterialTheme.colorScheme.onPrimary
                                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    )
                                                }
                                            }
                                        }

                                        // Pause row, only on Work tab
                                        if (showWork) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        start = 20.dp,
                                                        end = 20.dp,
                                                        top = 4.dp
                                                    ),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    text = if (isWorkPaused) "Work apps are paused" else "Work apps are active",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(50))
                                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                                        .clickable { onToggleWorkPaused() }
                                                        .padding(
                                                            horizontal = 14.dp,
                                                            vertical = 6.dp
                                                        ),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = if (isWorkPaused) "Resume" else "Pause",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        // No work profile — compact centered drag pill.
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(if (useHorizontalHeader) 24.dp else 32.dp)
                                                .clickable(
                                                    onClickLabel = "Close all apps",
                                                    onClick = onClose,
                                                ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .width(40.dp)
                                                    .height(4.dp)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.outline.copy(
                                                            alpha = 0.7f
                                                        ),
                                                        shape = CircleShape,
                                                    )
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(if (useHorizontalHeader) 4.dp else 8.dp))
                                }
                            }

                            // Search + Chips
                            if (useHorizontalHeader) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    DrawerSearchField(
                                        value = query,
                                        onValueChange = onQueryChange,
                                        focusManager = focusManager,
                                        apps = apps,
                                        onAppClick = onAppClickSafe,
                                        modifier = Modifier.weight(1f),
                                    )
                                    if (!hideChrome) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            DrawerChipsRow(
                                                chips = model.chips,
                                                selected = model.selected,
                                                onSelect = { filter = it },
                                                modifier = Modifier.fillMaxWidth(),
                                            )
                                        }
                                    }
                                }
                            } else {
                                DrawerSearchField(
                                    value = query,
                                    onValueChange = onQueryChange,
                                    focusManager = focusManager,
                                    apps = apps,
                                    onAppClick = onAppClickSafe,
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                )
                                if (!hideChrome) {
                                    Column {
                                        Spacer(Modifier.height(8.dp))
                                        DrawerChipsRow(
                                            chips = model.chips,
                                            selected = model.selected,
                                            onSelect = { filter = it },
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(if (useHorizontalHeader) 4.dp else 8.dp))
                        }

                        // Content
                        if (hasWorkProfile && showWork && isWorkPaused && query.isBlank()) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(horizontal = 32.dp),
                                ) {
                                    Text(
                                        text = "Work is paused",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                    )
                                    Text(
                                        text = "Work apps are hidden until you resume them.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(MaterialTheme.colorScheme.primary)
                                            .clickable { onToggleWorkPaused() }
                                            .padding(horizontal = 20.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "Resume work",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                        )
                                    }
                                }
                            }
                        } else {
                            when (settings.drawerLayout) {
                                DrawerLayout.List -> DrawerListContent(
                                    model = model,
                                    state = listState,
                                    nested = remember(sheet, listState) {
                                        SheetNestedScroll(sheet) { listState.canScrollBackward }
                                    },
                                    scrollbarSettings = softScrollbar,
                                    topHeaderPadding = if (useHorizontalHeader) 16.dp else 24.dp,
                                    settings = settings,
                                    onAppClick = onAppClickSafe,
                                    onAppLongClick = onAppLongClick,
                                )

                                DrawerLayout.Grid -> DrawerGridContent(
                                    model = model,
                                    state = gridState,
                                    nested = remember(sheet, gridState) {
                                        SheetNestedScroll(sheet) { gridState.canScrollBackward }
                                    },
                                    scrollbarSettings = softScrollbar,
                                    topHeaderPadding = if (useHorizontalHeader) 16.dp else 24.dp,
                                    columns = settings.drawerColumns,
                                    settings = settings.copy(itemLayout = AppItemLayout.TextBelow),
                                    onAppClick = onAppClickSafe,
                                    onAppLongClick = onAppLongClick,
                                )

                                DrawerLayout.HorizontalScroll -> DrawerHorizontalContent(
                                    model = model,
                                    columns = settings.drawerColumns,
                                    settings = settings.copy(itemLayout = AppItemLayout.TextBelow),
                                    sheet = sheet,
                                    onAppClick = onAppClickSafe,
                                    onAppLongClick = onAppLongClick,
                                )
                            }
                        }
                    }
                }
        }
    }
}