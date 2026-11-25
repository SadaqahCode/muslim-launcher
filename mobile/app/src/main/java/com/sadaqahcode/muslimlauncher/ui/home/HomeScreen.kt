package com.sadaqahcode.muslimlauncher.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadaqahcode.muslimlauncher.MainActivity
import com.sadaqahcode.muslimlauncher.domain.model.ToolId
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperMode
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperTextMode
import com.sadaqahcode.muslimlauncher.ui.common.layout.ScreenTopPadding
import com.sadaqahcode.muslimlauncher.ui.common.layout.rememberScreenLayout
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
import com.sadaqahcode.muslimlauncher.ui.home.strings.rememberHomeSnackbarStrings
import com.sadaqahcode.muslimlauncher.ui.theme.ProvideLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.Wallpaper
import com.sadaqahcode.muslimlauncher.ui.tools.ToolsPage
import com.sadaqahcode.muslimlauncher.ui.widgets.WidgetPage
import com.sadaqahcode.muslimlauncher.ui.widgets.WidgetPageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val WIDGET_PAGE = 0
private const val HOME_PAGE = 1
private const val TOOLS_PAGE = 2
private const val PAGE_COUNT = 3

private val SHADE_SWIPE_THRESHOLD = 80.dp
private const val SHEET_PROGRESS_EPSILON = 0.01f

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenSettings: () -> Unit,
    onOpenTool: (
        toolId: ToolId,
        wallpaperIsDark: Boolean,
        wallpaperTextMode: WallpaperTextMode,
        needsHalo: Boolean,
        adaptToWallpaper: Boolean,
    ) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val screen = rememberScreenLayout()

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

    val screenTopPadding = ScreenTopPadding.resolve(screen)

    LaunchedEffect(screen.widthDp, screen.heightDp) {
        viewModel.onScreenSizeChanged(screen.widthDp, screen.heightDp)
    }

    val effectivePageCount = if (screen.isLargeLandscape) 2 else PAGE_COUNT
    val homePageIndex = if (screen.isLargeLandscape) 0 else HOME_PAGE
    val toolsPageIndex = if (screen.isLargeLandscape) 1 else TOOLS_PAGE

    val pagerState = rememberPagerState(
        initialPage = homePageIndex,
        pageCount = { effectivePageCount },
    )
    val widgetPageVm: WidgetPageViewModel = hiltViewModel()

    var prevIsLargeLandscape by rememberSaveable {
        androidx.compose.runtime.mutableStateOf(screen.isLargeLandscape)
    }

    LaunchedEffect(screen.isLargeLandscape, effectivePageCount) {
        if (prevIsLargeLandscape == screen.isLargeLandscape) {
            // No mode change — just make sure the page is in bounds.
            if (pagerState.currentPage >= effectivePageCount) {
                pagerState.scrollToPage(homePageIndex)
            }
            return@LaunchedEffect
        }

        val current = pagerState.currentPage
        val target = if (screen.isLargeLandscape) {
            when (current) {
                HOME_PAGE -> 0
                TOOLS_PAGE -> 1
                WIDGET_PAGE -> 0
                else -> 0
            }
        } else {
            when (current) {
                0 -> HOME_PAGE
                1 -> TOOLS_PAGE
                else -> HOME_PAGE
            }
        }

        if (pagerState.currentPage != target) {
            pagerState.scrollToPage(target)
        }
        prevIsLargeLandscape = screen.isLargeLandscape
    }

    // i18n
    val snackbarStrings = rememberHomeSnackbarStrings()
    val currentSnackbarStrings by rememberUpdatedState(snackbarStrings)

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
            if (pagerState.currentPage != homePageIndex) pagerState.scrollToPage(homePageIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage, drawerVisible, selectedApp) {
        if (pagerState.currentPage != homePageIndex) return@LaunchedEffect
        if (drawerVisible) return@LaunchedEffect
        if (selectedApp != null) return@LaunchedEffect
        if (viewModel.isDefaultLauncher()) return@LaunchedEffect

        delay(400.milliseconds)
        if (snackbarHostState.currentSnackbarData != null) return@LaunchedEffect

        snackbarHostState.showLauncherSnackbar(
            message = currentSnackbarStrings.defaultLauncherMsg,
            actionLabel = currentSnackbarStrings.defaultLauncherAction,
            onAction = {
                context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
            },
        )
    }

    BackHandler(enabled = true) {
        when {
            selectedApp != null -> viewModel.dismissMenu()
            drawerVisible -> viewModel.closeDrawer()
            pagerState.currentPage != homePageIndex ->
                scope.launch { pagerState.animateScrollToPage(homePageIndex) }
            else -> {
                if (!viewModel.isDefaultLauncher()) {
                    activity?.moveTaskToBack(true)
                }
            }
        }
    }

    val homeNestedScroll = remember(sheet, pagerState, screen.isLargeLandscape, homePageIndex) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (pagerState.currentPage != homePageIndex &&
                    !(!screen.isLargeLandscape && pagerState.currentPage == WIDGET_PAGE)
                ) return Offset.Zero
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
                if (pagerState.currentPage != homePageIndex &&
                    !(!screen.isLargeLandscape && pagerState.currentPage == WIDGET_PAGE)
                ) return Velocity.Zero
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
                    (pagerState.currentPage == homePageIndex ||
                            (!screen.isLargeLandscape && pagerState.currentPage == WIDGET_PAGE)) &&
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
                                    LauncherSnackbarType.Warning,
                                )
                            }
                        }
                    }
                }
            )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val p = sheet.progress
                    val s = 1f - p * 0.04f
                    scaleX = s
                    scaleY = s
                    translationY = p * 12.dp.toPx()
                    clip = p > 0f
                    shape = RoundedCornerShape((p * 24).dp)
                }
        ) {
            Wallpaper(settings = userSettings, modifier = Modifier.fillMaxSize())

            Scaffold(containerColor = Color.Transparent) { innerPadding ->
                val contentModifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)

                BoxWithConstraints(contentModifier) {
                    val boxWidth = maxWidth
                    val showBottomBar = pagerState.currentPage != toolsPageIndex

                    val barHorizontalShift = when {
                        screen.isLandscape && !screen.isLargeLandscape &&
                                pagerState.currentPage == homePageIndex ->
                            boxWidth * 0.15f
                        screen.isLandscape && screen.isLargeLandscape ->
                            boxWidth * 0.25f
                        else -> 0.dp
                    }
                    val barWidthFraction =
                        if (screen.isLandscape && pagerState.currentPage == homePageIndex) 0.25f else 0.85f

                    Box(Modifier.fillMaxSize()) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            beyondViewportPageCount = effectivePageCount,
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
                                    homePageIndex -> {
                                        val content = if (screen.isLandscape) {
                                            @Composable {
                                                HomeContentLandscape(
                                                    modifier = Modifier.fillMaxSize(),
                                                    maxWidth = screen.contentMaxWidth,
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
                                                                pagerState.currentPage == homePageIndex
                                                    },
                                                    widgetContent = if (screen.isLargeLandscape) {
                                                        @Composable {
                                                            WidgetPage(
                                                                onBack = {},
                                                                paddingTop = screenTopPadding,
                                                                paddingTopEnabled = userSettings.paddingTopEnabled,
                                                                useCollapsingBar = !(screen.isLandscape && !screen.isLargeLandscape),
                                                                showIconBack = false,
                                                                onDoubleTapHome = viewModel::onDoubleTapHome,
                                                                viewModel = widgetPageVm,
                                                                topTransparent = screen.topTransparent,
                                                                pageActive = pagerState.currentPage == homePageIndex,
                                                            )
                                                        }
                                                    } else null,
                                                )
                                            }
                                        } else {
                                            @Composable {
                                                HomeContentPortrait(
                                                    modifier = Modifier.fillMaxSize(),
                                                    maxWidth = screen.contentMaxWidth,
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
                                                                pagerState.currentPage == homePageIndex
                                                    },
                                                )
                                            }
                                        }
                                        val homeAlpha by animateFloatAsState(
                                            targetValue = if (drawerVisible) 0f else 1f,
                                            animationSpec = tween(250),
                                            label = "homeAlpha",
                                        )

                                        Box(
                                            Modifier
                                                .fillMaxSize()
                                                .alpha(homeAlpha)
                                        ) {
                                            content()
                                        }
                                    }

                                    toolsPageIndex -> {
                                        ToolsPage(
                                            textScale = userSettings.textSize,
                                            paddingTop = screenTopPadding,
                                            paddingTopEnabled = userSettings.paddingTopEnabled,
                                            useCollapsingBar = !(screen.isLandscape && !screen.isLargeLandscape),
                                            onBack = {
                                                scope.launch { pagerState.animateScrollToPage(homePageIndex) }
                                            },
                                            onDoubleTapHome = viewModel::onDoubleTapHome,
                                            onOpenTool = { toolId ->
                                                onOpenTool(
                                                    toolId,
                                                    wallpaperIsDark,
                                                    userSettings.wallpaperTextMode,
                                                    userSettings.wallpaperMode == WallpaperMode.Image,
                                                    userSettings.wallpaperMode != WallpaperMode.System,
                                                )
                                            },
                                            pageActive = pagerState.currentPage == toolsPageIndex,
                                        )
                                    }

                                    else -> {
                                        val widgetAlpha by animateFloatAsState(
                                            targetValue = if (drawerVisible) 0f else 1f,
                                            animationSpec = tween(250),
                                            label = "widgetAlpha",
                                        )

                                        Box(
                                            Modifier
                                                .fillMaxSize()
                                                .alpha(widgetAlpha)
                                        ) {
                                            WidgetPage(
                                                onBack = {
                                                    scope.launch { pagerState.animateScrollToPage(homePageIndex) }
                                                },
                                                paddingTop = screenTopPadding,
                                                topTransparent = screen.topTransparent,
                                                paddingTopEnabled = userSettings.paddingTopEnabled,
                                                useCollapsingBar = !(screen.isLandscape && !screen.isLargeLandscape),
                                                onDoubleTapHome = viewModel::onDoubleTapHome,
                                                viewModel = widgetPageVm,
                                                pageActive = pagerState.currentPage == WIDGET_PAGE,
                                            )
                                        }
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
                                        .offset(x = barHorizontalShift)
                                        .fillMaxWidth(barWidthFraction)
                                        .padding(bottom = 16.dp)
                                        .height(22.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        if (drawerShown) {
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
                        topTransparent = screen.topTransparent,
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
                .align(Alignment.BottomEnd)
                .padding(bottom = 52.dp)
                .zIndex(100f),
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