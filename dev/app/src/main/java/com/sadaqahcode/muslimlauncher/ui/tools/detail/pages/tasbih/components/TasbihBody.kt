package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.ui.components.AdaptiveSidebarLayout
import com.sadaqahcode.muslimlauncher.ui.home.PageMaxWidthLargeScreen
import com.sadaqahcode.muslimlauncher.ui.home.PageMaxWidthScreen
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.Actions
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.TasbihUiState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.freeKey
import kotlin.collections.orEmpty

@Composable
fun TasbihBody(
    toolTitle: String,
    state: TasbihUiState,
    language: String,
    actions: Actions,
    onClose: () -> Unit,
    colors: ColorScheme,
) {
    var showHapticDialog by remember { mutableStateOf(false) }
    var showLibrary by remember { mutableStateOf(false) }
    var showReader by remember { mutableStateOf(false) }
    val dhikr = state.activeDhikr ?: return
    val item = state.currentItem

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 840.dp && maxHeight >= 500.dp
        val isSplit = maxWidth > maxHeight && maxWidth >= 480.dp
        val isLandscape = maxWidth > maxHeight
        val isLargeLandscape = isLandscape && maxWidth >= 1200.dp
        val contentMaxWidth = if (isLargeLandscape) {
            PageMaxWidthLargeScreen
        } else {
            PageMaxWidthScreen
        }

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
                    drawerOpen = showLibrary,
                    onDrawerClose = { showLibrary = false }, // close without pick = keep prev data, no reset
                    sidebarWidth = 320.dp,
                    sidebarContent = {
                        LibraryContent(
                            state = state,
                            language = language,
                            onPickFree = { key ->
                                actions.onSelectFree(key) // this already sets mode=Free, finished=false
                                showLibrary = false
                            },
                            onPickGuided = { index ->
                                actions.onStartGuided(index) // this already sets mode=Guided, entry=0, finished=false
                                showLibrary = false
                            },
                            onToggleFavFree = actions.onToggleFavFree,
                            onToggleFavGuided = actions.onToggleFavGuided,
                            modifier = Modifier.fillMaxSize(),
                        )
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
                            actions = actions,
                            onOpenLibrary = { showLibrary = true },
                            onOpenReader = { showReader = true },
                            onClose = onClose,
                            onOpenHapticDialog = { showHapticDialog = true },
                            // Prev finished data stays until user actually picks
                            onDone = { showLibrary = true },
                            modifier = Modifier.fillMaxSize(),
                            colors = colors,
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
    }
}