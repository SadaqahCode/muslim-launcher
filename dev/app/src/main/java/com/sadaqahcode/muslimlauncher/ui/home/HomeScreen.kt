package com.sadaqahcode.muslimlauncher.ui.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sadaqahcode.muslimlauncher.MainActivity
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperMode
import com.sadaqahcode.muslimlauncher.ui.components.AppContextMenu
import com.sadaqahcode.muslimlauncher.ui.components.LauncherSnackbarHost
import com.sadaqahcode.muslimlauncher.ui.components.LauncherSnackbarType
import com.sadaqahcode.muslimlauncher.ui.components.showLauncherSnackbar
import com.sadaqahcode.muslimlauncher.ui.drawer.AppDrawer
import com.sadaqahcode.muslimlauncher.ui.drawer.rememberDrawerSheetState
import com.sadaqahcode.muslimlauncher.ui.home.components.AccessibilityDialogKind
import com.sadaqahcode.muslimlauncher.ui.home.components.AccessibilityFallbackDialog
import com.sadaqahcode.muslimlauncher.ui.home.components.BottomBar
import com.sadaqahcode.muslimlauncher.ui.home.components.HomeContentLandscape
import com.sadaqahcode.muslimlauncher.ui.home.components.HomeContentPortrait
import com.sadaqahcode.muslimlauncher.ui.home.helper.ShadeOpenResult
import com.sadaqahcode.muslimlauncher.ui.home.helper.openNotificationShade
import com.sadaqahcode.muslimlauncher.ui.home.helper.shadeSwipeGesture
import com.sadaqahcode.muslimlauncher.ui.theme.ProvideLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.Wallpaper
import com.sadaqahcode.muslimlauncher.ui.tools.ToolsPage
import com.sadaqahcode.muslimlauncher.ui.tools.ToolsPageViewModel
import com.sadaqahcode.muslimlauncher.ui.widgets.WidgetPage
import com.sadaqahcode.muslimlauncher.ui.widgets.WidgetPageViewModel
import kotlinx.coroutines.launch

private const val WIDGET_PAGE = 0
private const val HOME_PAGE = 1
private const val TOOLS_PAGE = 2
private const val PAGE_COUNT = 3

private val SHADE_SWIPE_THRESHOLD = 80.dp
private const val SHEET_PROGRESS_EPSILON = 0.01f

private const val MAX_SCRIM_ALPHA = 0.45f

private val BOTTOM_BAR_HEIGHT = 24.dp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenSettings: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val heroState by viewModel.heroState.collectAsStateWithLifecycle()
    val homeApps by viewModel.homeApps.collectAsStateWithLifecycle()
    val dockApps by viewModel.dockAppList.collectAsStateWithLifecycle()
    val drawerApps by viewModel.drawerApps.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val userSettings by viewModel.settings.collectAsStateWithLifecycle()
    val wallpaperIsDark by viewModel.wallpaperIsDark.collectAsStateWithLifecycle()

    val showDialog by viewModel.showAccessibilityDialog.collectAsState()
    val dialogKind by viewModel.dialogKind.collectAsState()
    val shadeEnabled by viewModel.shadeAccessibilityEnabled.collectAsState()
    val accessibilityEnabled by viewModel.accessibilityEnabled.collectAsState()
    val deviceAdminEnabled by viewModel.deviceAdminEnabled.collectAsState()

    val drawerVisible by viewModel.drawerVisible.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedApp by viewModel.selectedApp.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    // Work profile: call state from ViewModel
    val hasWorkProfile by viewModel.hasWorkProfile.collectAsStateWithLifecycle()
    val isWorkPaused by viewModel.isWorkPaused.collectAsStateWithLifecycle()
    val workApps by viewModel.workApps.collectAsStateWithLifecycle()
    val personalApps by viewModel.personalApps.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val sheet = rememberDrawerSheetState(
        open = drawerVisible,
        onOpenRequested = viewModel::openDrawer,
        onCloseRequested = viewModel::closeDrawer,
    )
    val drawerShown by remember { derivedStateOf { sheet.progress > 0f } }

    val pagerState = rememberPagerState(initialPage = HOME_PAGE, pageCount = { PAGE_COUNT })
    val widgetPageVm: WidgetPageViewModel = viewModel()
    val toolsVm: ToolsPageViewModel = viewModel()

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showLauncherSnackbar(it, LauncherSnackbarType.Info)
            viewModel.consumeMessage()
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refreshAccessibilityState()
        viewModel.refreshWorkProfileState()
    }

    val activity = LocalActivity.current as? MainActivity
    val homePressCount = activity?.homePressCount?.collectAsStateWithLifecycle()?.value ?: 0L
    var lastHandledHomePress by remember { mutableLongStateOf(homePressCount) }

    LaunchedEffect(homePressCount) {
        if (homePressCount > lastHandledHomePress) {
            lastHandledHomePress = homePressCount
            if (viewModel.drawerVisible.value) viewModel.closeDrawer()
            if (viewModel.selectedApp.value != null) viewModel.dismissMenu()
            if (pagerState.currentPage != HOME_PAGE) pagerState.scrollToPage(HOME_PAGE)
        }
    }

    BackHandler(enabled = true) {
        when {
            selectedApp != null -> viewModel.dismissMenu()
            drawerVisible -> viewModel.closeDrawer()
            pagerState.currentPage != HOME_PAGE ->
                scope.launch { pagerState.animateScrollToPage(HOME_PAGE) }
            else -> {
                if (!viewModel.isDefaultLauncher()) {
                    activity?.moveTaskToBack(true)
                }
            }
        }
    }

    val homeNestedScroll = remember(sheet, pagerState) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (pagerState.currentPage != HOME_PAGE) return Offset.Zero
                if (source == NestedScrollSource.UserInput &&
                    available.y < 0f &&
                    sheet.progress < 1f
                ) {
                    val used = sheet.dragBy(-available.y)
                    return Offset(0f, -used)
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity,
            ): Velocity {
                if (pagerState.currentPage != HOME_PAGE) return Velocity.Zero
                if (sheet.progress > 0f) {
                    sheet.settle(available.y)
                    return available
                }
                return Velocity.Zero
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .onSizeChanged { sheet.heightPx = it.height.toFloat() }
            .nestedScroll(homeNestedScroll)
            .shadeSwipeGesture(
                threshold = SHADE_SWIPE_THRESHOLD,
                isGatingOk = {
                    pagerState.currentPage == HOME_PAGE &&
                            !drawerVisible &&
                            selectedApp == null &&
                            sheet.progress <= SHEET_PROGRESS_EPSILON
                },
                onSwipeTriggered = {
                    when (openNotificationShade(context)) {
                        ShadeOpenResult.OpenedSilently,
                        ShadeOpenResult.OpenedViaAccessibility -> Unit

                        ShadeOpenResult.NeedsAccessibilityPermission,
                        ShadeOpenResult.Failed -> {
                            viewModel.requestShadeAccessibility()
                        }

                        ShadeOpenResult.EnabledButNotBound -> {
                            scope.launch {
                                snackbarHostState.showLauncherSnackbar(
                                    "Almost ready - try the swipe again in a moment",
                                    LauncherSnackbarType.Warning
                                )
                            }
                        }
                    }
                }
            )
    ) {
        Wallpaper(settings = userSettings, modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
        ) { innerPadding ->
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

            BoxWithConstraints(contentModifier) {
                val isLandscape = maxWidth > maxHeight
                val showBottomBar = pagerState.currentPage == HOME_PAGE

                Box(Modifier.fillMaxSize()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        beyondViewportPageCount = PAGE_COUNT,
                        flingBehavior = PagerDefaults.flingBehavior(
                            state = pagerState,
                            snapAnimationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = 700f,
                                visibilityThreshold = 0.001f,
                            ),
                        ),
                    ) { page ->
                        ProvideLegibleColors(
                            wallpaperIsDark = wallpaperIsDark,
                            mode = userSettings.wallpaperTextMode,
                            needsHalo = userSettings.wallpaperMode == WallpaperMode.Image,
                            adaptToWallpaper = userSettings.wallpaperMode != WallpaperMode.System,
                        ) {
                            when (page) {
                                HOME_PAGE -> {
                                    val content = if (isLandscape) {
                                        @Composable {
                                            HomeContentLandscape(
                                                modifier = Modifier.fillMaxSize(),
                                                heroState = heroState,
                                                homeApps = homeApps,
                                                dockApps = dockApps,
                                                loading = loading,
                                                settings = userSettings,
                                                onDoubleTapHome = viewModel::onDoubleTapHome,
                                                onOpenSettings = onOpenSettings,
                                                onLaunch = viewModel::launch,
                                                onAppLongClick = viewModel::selectAppForMenu,
                                                onOpenDrawer = viewModel::openDrawer,
                                                isGatingOk = {
                                                    selectedApp == null &&
                                                            !drawerVisible &&
                                                            pagerState.currentPage == HOME_PAGE
                                                },
                                            )
                                        }
                                    } else {
                                        @Composable {
                                            HomeContentPortrait(
                                                modifier = Modifier.fillMaxSize(),
                                                heroState = heroState,
                                                homeApps = homeApps,
                                                dockApps = dockApps,
                                                loading = loading,
                                                settings = userSettings,
                                                onDoubleTapHome = viewModel::onDoubleTapHome,
                                                onOpenSettings = onOpenSettings,
                                                onLaunch = viewModel::launch,
                                                onAppLongClick = viewModel::selectAppForMenu,
                                                onOpenDrawer = viewModel::openDrawer,
                                                isGatingOk = {
                                                    selectedApp == null &&
                                                            !drawerVisible &&
                                                            pagerState.currentPage == HOME_PAGE
                                                },
                                            )
                                        }
                                    }
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(bottom = BOTTOM_BAR_HEIGHT)
                                    ) {
                                        content()
                                    }
                                }
                                WIDGET_PAGE -> {
                                    WidgetPage(
                                        onBack = {
                                            scope.launch { pagerState.animateScrollToPage(HOME_PAGE) }
                                        },
                                        onDoubleTapHome = viewModel::onDoubleTapHome,
                                        viewModel = widgetPageVm,
                                        pageActive = pagerState.currentPage == WIDGET_PAGE,
                                    )
                                }
                                TOOLS_PAGE -> {
                                    ToolsPage(
                                        onBack = {
                                            scope.launch { pagerState.animateScrollToPage(HOME_PAGE) }
                                        },
                                        viewModel = toolsVm,
                                        onDoubleTapHome = viewModel::onDoubleTapHome,
                                        onOpenTool = { /* TODO: navigate to per-tool screen */ },
                                    )
                                }
                            }
                        }
                    }

                    if (showBottomBar) {
                        ProvideLegibleColors(
                            wallpaperIsDark = wallpaperIsDark,
                            mode = userSettings.wallpaperTextMode,
                            needsHalo = userSettings.wallpaperMode == WallpaperMode.Image,
                            adaptToWallpaper = userSettings.wallpaperMode != WallpaperMode.System,
                        ) {
                            BottomBar(
                                onOpenDrawer = viewModel::openDrawer,
                                animate = !drawerShown,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(BOTTOM_BAR_HEIGHT),
                            )
                        }
                    }
                }
            }
        }

        if (drawerShown) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.scrim.copy(
                            alpha = sheet.progress * MAX_SCRIM_ALPHA,
                        )
                    )
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = (1f - sheet.progress) * size.height
                        alpha = (sheet.progress * 3f).coerceIn(0f, 1f)
                    }
            ) {
                ProvideLegibleColors(
                    wallpaperIsDark = wallpaperIsDark,
                    mode = userSettings.wallpaperTextMode,
                    needsHalo = userSettings.wallpaperMode == WallpaperMode.Image,
                    adaptToWallpaper = userSettings.wallpaperMode != WallpaperMode.System,
                ) {
                    AppDrawer(
                        apps = drawerApps,
                        query = searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        onAppClick = viewModel::launch,
                        onAppLongClick = viewModel::selectAppForMenu,
                        onClose = viewModel::closeDrawer,
                        sheet = sheet,
                        settings = userSettings,
                        hasWorkProfile = hasWorkProfile,
                        isWorkPaused = isWorkPaused,
                        workApps = workApps,
                        personalApps = personalApps,
                        onToggleWorkPaused = viewModel::toggleWorkPaused,
                    )
                }
            }
        }

        selectedApp?.let { app ->
            AppContextMenu(
                app = app,
                isFavorite = app.isPinned,
                isInDock = app.isDocked,
                onDismiss = viewModel::dismissMenu,
                settings = userSettings,
                onToggleFavorite = { viewModel.toggleFavorite(app) },
                onToggleDock = { viewModel.toggleDock(app) },
                onCategorySelect = { cat -> viewModel.setCategory(app, cat) },
                onAppInfo = { viewModel.openAppInfo(app) },
                onUninstall = { viewModel.requestUninstall(app) },
            )
        }

        LauncherSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = BOTTOM_BAR_HEIGHT + 16.dp
                )
                .zIndex(100f)
        )
    }

    if (showDialog) {
        AccessibilityFallbackDialog(
            onDismiss = { viewModel.dismissAccessibilityDialog() },
            onOpenSettings = { viewModel.onAccessibilityDialogConfirm() },
            onRequestDeviceAdmin = { activity?.let { viewModel.requestDeviceAdmin(it) } },
            kind = dialogKind,
            isAccessibilityEnabled = if (dialogKind == AccessibilityDialogKind.Shade) shadeEnabled else accessibilityEnabled,
            isDeviceAdminEnabled = deviceAdminEnabled,
        )
    }
}