package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.Dhikr
import com.sadaqahcode.muslimlauncher.ui.common.layout.PageMaxWidthLargeScreen
import com.sadaqahcode.muslimlauncher.ui.common.layout.PageMaxWidthScreen
import com.sadaqahcode.muslimlauncher.ui.common.layout.ScreenLayout
import com.sadaqahcode.muslimlauncher.ui.components.AdaptiveSidebarLayout
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.Actions
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.TasbihUiState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.freeKey
import kotlinx.coroutines.delay
import kotlin.collections.orEmpty
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TasbihBody(
    toolTitle: String,
    state: TasbihUiState,
    language: String,
    actions: Actions,
    onClose: () -> Unit,
    screen: ScreenLayout,
    colors: ColorScheme,
    onboardingDone: Boolean = false,
) {
    val topTransparentModal = screen.topTransparent

    var showHapticDialog by remember { mutableStateOf(false) }
    var showLibrary by remember { mutableStateOf(false) }
    var showReader by remember { mutableStateOf(false) }

    var libraryAnimTrigger by remember { mutableIntStateOf(0) }
    val libraryAlpha = remember { Animatable(0f) }

    // Guards the onDone reopen race right after a pick.
    var justPicked by remember { mutableStateOf(false) }
    LaunchedEffect(justPicked) {
        if (justPicked) {
            delay(400.milliseconds)
            justPicked = false
        }
    }

    LaunchedEffect(libraryAnimTrigger) {
        libraryAlpha.snapTo(0f)
        libraryAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 420,
                easing = FastOutSlowInEasing,
            ),
        )
    }

    val dhikr = state.activeDhikr ?: return
    val item = state.currentItem

    val isTablet = screen.isTablet
    val isSplit = screen.isSplit
    val permanentSidebar = screen.isLargeLandscape
    val contentMaxWidth = if (permanentSidebar) {
        PageMaxWidthLargeScreen
    } else {
        PageMaxWidthScreen
    }

    // ── GuideKit target bounds ─────────────────────────────────────────────
    var pickerBounds by remember { mutableStateOf<Rect?>(null) }
    var libraryPickerBounds by remember { mutableStateOf<Rect?>(null) }
    var favoriteBounds by remember { mutableStateOf<Rect?>(null) }
    var hapticBounds by remember { mutableStateOf<Rect?>(null) }
    var counterBounds by remember { mutableStateOf<Rect?>(null) }
    val usesPermanentSidebar = isTablet && permanentSidebar

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            Modifier
                .widthIn(max = contentMaxWidth)
                .fillMaxSize()
        ) {
            AdaptiveSidebarLayout(
                isTablet = isTablet,
                permanentSidebar = permanentSidebar,
                drawerOpen = showLibrary,
                onDrawerClose = { showLibrary = false },
                onDrawerOpen  = { showLibrary = true },
                sidebarWidth = 320.dp,
                sidebarWindowInsets = WindowInsets.systemBars,
                contentWindowInsets = WindowInsets(0),
                sidebarContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .alpha(libraryAlpha.value)
                    ) {
                        LibraryContent(
                            state = state,
                            language = language,
                            onPickFree = { key ->
                                justPicked = true
                                actions.onSelectFree(key)
                                showLibrary = false
                            },
                            onPickGuided = { index ->
                                justPicked = true
                                actions.onStartGuided(index)
                                showLibrary = false
                            },
                            onToggleFavFree = actions.onToggleFavFree,
                            onToggleFavGuided = actions.onToggleFavGuided,
                            modifier = Modifier.fillMaxSize(),
                            onLibraryPickerBounds = { libraryPickerBounds = it },
                        )
                    }
                },
                content = {
                    MainPane(
                        toolTitle = toolTitle,
                        state = state,
                        item = item,
                        dhikr = dhikr,
                        language = language,
                        split = isTablet || isSplit,
                        showPicker = !isTablet,
                        showSidebar = permanentSidebar,
                        actions = actions,
                        onOpenLibrary = { showLibrary = true },
                        onOpenReader = { showReader = true },
                        onClose = onClose,
                        onOpenHapticDialog = { showHapticDialog = true },
                        onDone = {
                            if (!justPicked) {
                                showLibrary = true
                                libraryAnimTrigger++
                            }
                        },
                        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
                        colors = colors,
                        onPickerBounds = { pickerBounds = it },
                        onFavoriteBounds = { favoriteBounds = it },
                        onHapticBounds = { hapticBounds = it },
                        onCounterBounds = { counterBounds = it },
                    )
                },
            )
        }
    }

    if (showReader) {
        val readerList: List<Dhikr> = if (state.autoRun) {
            item?.entries?.map { it.dhikr }.orEmpty()
        } else {
            state.freeDisplay
        }
        if (readerList.isNotEmpty()) {
            val start = if (state.autoRun) {
                state.entryIndex
            } else {
                readerList.indexOfFirst { it.freeKey() == dhikr.freeKey() }.coerceAtLeast(0)
            }
            ReaderSheet(
                dhikrs = readerList,
                startIndex = start,
                selectable = !state.autoRun,
                currentKey = dhikr.freeKey(),
                favorites = state.favoriteFree,
                language = language,
                onUse = { d -> actions.onSelectFree(d.freeKey()) },
                onToggleFavorite = { d -> actions.onToggleFavFree(d.freeKey()) },
                topTransparentModal = topTransparentModal,
                onDismiss = { showReader = false },
            )
        } else {
            showReader = false
        }
    }

    if (showHapticDialog) {
        HapticFeedbackDialog(
            current = state.hapticMode,
            language = language,
            onSelect = { mode ->
                actions.onSetHapticMode(mode)
                showHapticDialog = false
            },
            onDismiss = { showHapticDialog = false },
        )
    }

        TasbihGuide(
            visible = onboardingDone,
            language = language,
            libraryPickerBounds = libraryPickerBounds,
            permanentSidebar = usesPermanentSidebar,
            pickerBounds = pickerBounds,
            favoriteBounds = favoriteBounds,
            hapticBounds = hapticBounds,
            counterBounds = counterBounds,
            onComplete = actions.onCompleteOnboarding,
            style = rememberTasbihGuideStyle(colors),
            modifier = Modifier.fillMaxSize(),
        )
}