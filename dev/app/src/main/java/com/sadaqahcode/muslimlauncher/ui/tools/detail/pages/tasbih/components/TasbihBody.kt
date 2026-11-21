package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.ui.components.ScrimBottomSheet
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.Actions
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.TasbihUiState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.freeKey
import kotlin.collections.orEmpty



private enum class TasbihLayout { Stack, Split, Library }

@OptIn(ExperimentalMaterial3Api::class)
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
        val layout = when {
            maxWidth >= 840.dp && maxHeight >= 500.dp -> TasbihLayout.Library
            maxWidth > maxHeight && maxWidth >= 480.dp -> TasbihLayout.Split
            else -> TasbihLayout.Stack
        }

        when (layout) {
            TasbihLayout.Library -> Row(Modifier.fillMaxSize()) {
                LibraryContent(
                    state = state,
                    language = language,
                    onPickFree = actions.onSelectFree,
                    onPickGuided = actions.onStartGuided,
                    onToggleFavFree = actions.onToggleFavFree,
                    onToggleFavGuided = actions.onToggleFavGuided,
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight(),
                )
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(colors.outline.copy(alpha = 0.15f))
                )
                MainPane(
                    toolTitle = toolTitle,
                    state = state,
                    item = item,
                    dhikr = dhikr,
                    language = language,
                    split = true,
                    showPicker = false,
                    actions = actions,
                    onOpenLibrary = {},
                    onOpenReader = { showReader = true },
                    onClose = onClose,
                    onOpenHapticDialog = { showHapticDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = colors
                )
            }

            TasbihLayout.Split -> MainPane(
                toolTitle = toolTitle,
                state = state,
                item = item,
                dhikr = dhikr,
                language = language,
                split = true,
                showPicker = true,
                actions = actions,
                onOpenLibrary = { showLibrary = true },
                onOpenReader = { showReader = true },
                onClose = onClose,
                onOpenHapticDialog = { showHapticDialog = true },
                modifier = Modifier.fillMaxSize(),
                colors = colors,
            )

            TasbihLayout.Stack -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                MainPane(
                    toolTitle = toolTitle,
                    state = state,
                    item = item,
                    dhikr = dhikr,
                    language = language,
                    split = false,
                    showPicker = true,
                    actions = actions,
                    onOpenLibrary = { showLibrary = true },
                    onOpenReader = { showReader = true },
                    onClose = onClose,
                    onOpenHapticDialog = { showHapticDialog = true },
                    modifier = Modifier
                        .widthIn(max = 640.dp)
                        .fillMaxSize(),
                    colors = colors,
                )
            }
        }

        // Library: sheets everywhere except where rails already display them.
        if (showLibrary && layout != TasbihLayout.Library) {
            ScrimBottomSheet(
                onDismiss = { showLibrary = false },
            ) {
                LibraryContent(
                    state = state,
                    language = language,
                    onPickFree = { key ->
                        actions.onSelectFree(key)
                        showLibrary = false
                    },
                    onPickGuided = { index ->
                        actions.onStartGuided(index)
                        showLibrary = false
                    },
                    onToggleFavFree = actions.onToggleFavFree,
                    onToggleFavGuided = actions.onToggleFavGuided,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Full reader: word + Arabic + Latin + meaning + reference, swipeable.
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
