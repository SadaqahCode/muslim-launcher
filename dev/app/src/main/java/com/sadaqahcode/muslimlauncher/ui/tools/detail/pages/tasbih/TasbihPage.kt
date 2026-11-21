package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import com.sadaqahcode.muslimlauncher.domain.model.tools.HapticMode
import com.sadaqahcode.muslimlauncher.domain.model.tools.name
import com.sadaqahcode.muslimlauncher.domain.model.tools.text
import com.sadaqahcode.muslimlauncher.domain.model.tools.title
import com.sadaqahcode.muslimlauncher.ui.components.ScrimBottomSheet
import com.sadaqahcode.muslimlauncher.ui.components.ScrimDialog
import com.sadaqahcode.muslimlauncher.ui.theme.ArabicType
import com.sadaqahcode.muslimlauncher.ui.theme.GlassEmphasis
import com.sadaqahcode.muslimlauncher.ui.theme.glass

private class Actions(
    val onTap: () -> Unit,
    val onReset: () -> Unit,
    val onSelectFree: (String) -> Unit,
    val onMoveFree: (Int) -> Unit,
    val onToggleFavFree: (String) -> Unit,
    val onToggleFavGuided: (String) -> Unit,
    val onSelectTarget: (Int) -> Unit,
    val onStartGuided: (Int) -> Unit,
    val onRestartGuided: () -> Unit,
    val onExitGuided: () -> Unit,
    val onNext: () -> Unit,
    val onPrevious: () -> Unit,
    val onRetry: () -> Unit,
    val onSetHapticMode: (HapticMode) -> Unit,
)

private enum class TasbihLayout { Stack, Split, Library }

/** Free mode target. 0 = no target. Aligns with TasbihViewModel.selectTarget. */
private val Targets = listOf(33, 99, 100, 0)

private fun Dhikr.latinText(): String = latin.orEmpty()
private fun Dhikr.referenceText(): String = reference.orEmpty()

private fun Dhikr.displayTitle(language: String): String = title(language).ifEmpty { arabic }

@Composable
internal fun TasbihPage(
    tool: Tool,
    onClose: () -> Unit,
    viewModel: TasbihViewModel = hiltViewModel(),
) {
    val colors = MaterialTheme.colorScheme
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val language = LocalLocale.current.platformLocale.language
    val vibrator = remember(context) { context.vibratorOrNull() }

    BackHandler(enabled = state.autoRun) { viewModel.exitGuided() }

    LaunchedEffect(viewModel, vibrator) {
        viewModel.haptics.collect { event ->
            when (viewModel.state.value.hapticMode) {
                HapticMode.Haptic -> haptic.performHapticFeedback(event.hapticFeedbackType())
                HapticMode.Vibration -> vibrator?.vibrateCompat(event)
                HapticMode.Silent -> Unit
            }
        }
    }

    val actions = remember(viewModel) {
        Actions(
            onTap = viewModel::tap,
            onReset = viewModel::requestReset,
            onSelectFree = viewModel::selectFree,
            onMoveFree = viewModel::moveFree,
            onToggleFavFree = viewModel::toggleFavoriteFree,
            onToggleFavGuided = viewModel::toggleFavoriteGuided,
            onSelectTarget = viewModel::selectTarget,
            onStartGuided = viewModel::startGuided,
            onRestartGuided = viewModel::restartGuided,
            onExitGuided = viewModel::exitGuided,
            onNext = viewModel::nextEntry,
            onPrevious = viewModel::previousEntry,
            onRetry = viewModel::retry,
            onSetHapticMode = viewModel::setHapticMode,
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        contentColor = colors.onBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            ) {
                if (state.isRefreshing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxSize(),
                        color = colors.primary,
                        trackColor = colors.primary.copy(alpha = 0.15f),
                    )
                }
            }
            Box(Modifier.weight(1f)) {
                TasbihContent(
                    toolTitle = stringResource(tool.titleRes),
                    state = state,
                    language = language,
                    actions = actions,
                    onClose = onClose,
                )
            }
        }
    }
}

@Composable
private fun TasbihContent(
    toolTitle: String,
    state: TasbihUiState,
    language: String,
    actions: Actions,
    onClose: () -> Unit,
) {
    when {
        state.items.isEmpty() && state.error != null -> MessagePane(
            title = toolTitle,
            message = state.error,
            language = language,
            onClose = onClose,
            onRetry = actions.onRetry,
        )

        state.items.isEmpty() -> MessagePane(
            title = toolTitle,
            message = tr(language, "Dzikir tidak tersedia.", "No dhikr available."),
            language = language,
            onClose = onClose,
            onRetry = null,
        )

        else -> TasbihBody(toolTitle, state, language, actions, onClose)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasbihBody(
    toolTitle: String,
    state: TasbihUiState,
    language: String,
    actions: Actions,
    onClose: () -> Unit,
) {
    var showHapticDialog by remember { mutableStateOf(false) }
    var showLibrary by remember { mutableStateOf(false) }
    var showReader by remember { mutableStateOf(false) }
    val dhikr = state.activeDhikr ?: return
    val item = state.currentItem
    val colors = MaterialTheme.colorScheme

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

@Composable
private fun MainPane(
    toolTitle: String,
    state: TasbihUiState,
    item: DhikrItem?,
    dhikr: Dhikr,
    language: String,
    split: Boolean,
    showPicker: Boolean,
    actions: Actions,
    onOpenLibrary: () -> Unit,
    onOpenReader: () -> Unit,
    onClose: () -> Unit,
    onOpenHapticDialog: () -> Unit,
    modifier: Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val guided = state.autoRun && item != null
    val finished = guided && state.finished

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        PageHeader(language, onClose) {
            val pickerModifier = if (showPicker) {
                Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(
                        role = Role.Button,
                        onClickLabel = tr(language, "Pilih dzikir", "Choose dhikr"),
                        onClick = onOpenLibrary,
                    )
            } else {
                Modifier
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(pickerModifier)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = toolTitle,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant,
                    )
                    if (showPicker) {
                        Text(
                            text = "•  " + tr(language, "Ganti dzikir", "Change dhikr"),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.primary,
                            modifier = Modifier.padding(start = 6.dp),
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (guided) item.name(language) else dhikr.displayTitle(language),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (showPicker) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
            if (guided) {
                GuidedBadge(language, actions.onExitGuided)
            }
            FavoriteButton(
                favorite = state.isFavoriteCurrent,
                language = language,
                onClick = {
                    if (guided) item.let { actions.onToggleFavGuided(it.guidedKey()) }
                    else actions.onToggleFavFree(dhikr.freeKey())
                },
            )
            IconButton(onClick = onOpenHapticDialog) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = tr(language, "Umpan balik sentuhan", "Touch feedback"),
                    tint = colors.onSurfaceVariant,
                )
            }
        }

        if (guided) {
            GuidedProgress(
                progress = state.progress,
                stepLabel = "${tr(language, "Langkah", "Step")} ${state.entryIndex + 1}/${item.entries.size}",
                totalLabel = "${state.doneRepetitions}/${state.totalRepetitions}",
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        if (finished) {
            FinishPanel(
                item = item,
                language = language,
                onRepeat = actions.onRestartGuided,
                onDone = actions.onExitGuided,
                modifier = Modifier.weight(1f),
            )
            return@Column
        }

        val freeList = state.freeDisplay
        val freeIndex = freeList.indexOfFirst { it.freeKey() == dhikr.freeKey() }

        val words: @Composable (Modifier) -> Unit = { m ->
            WordsPane(
                dhikr = dhikr,
                language = language,
                swipeEnabled = !state.autoRun && freeList.size > 1,
                onPrevious = { actions.onMoveFree(-1) },
                onNext = { actions.onMoveFree(1) },
                onExpand = onOpenReader,
                guidedItem = if (guided) item else null,
                guidedEntryIndex = state.entryIndex,
                position = if (!state.autoRun && freeIndex >= 0 && freeList.size > 1) {
                    "${freeIndex + 1} / ${freeList.size}"
                } else {
                    null
                },
                modifier = m,
            )
        }
        val counter: @Composable (Modifier) -> Unit = { m ->
            TapCounter(state.count, state.target, language, actions.onTap, m)
        }
        val dock: @Composable () -> Unit = {
            ControlDock(
                state = state,
                language = language,
                actions = actions,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
            )
        }

        if (split) {
            Row(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                words(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    counter(Modifier.weight(1f))
                    dock()
                }
            }
        } else {
            words(Modifier.weight(0.8f))
            counter(Modifier.weight(1.2f))
            dock()
        }
    }
}


@Composable
private fun PageHeader(
    language: String,
    onClose: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                contentDescription = tr(language, "Kembali", "Back"),
            )
        }
        content()
    }
}

@Composable
private fun FavoriteButton(favorite: Boolean, language: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (favorite) {
                tr(language, "Lepas dari favorit", "Remove from favorites")
            } else {
                tr(language, "Sematkan ke favorit", "Pin to favorites")
            },
            tint = if (favorite) colors.onSurface else colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun GuidedBadge(language: String, onExit: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .clickable(
                role = Role.Button,
                onClickLabel = tr(language, "Keluar ke mode bebas", "Exit to free mode"),
                onClick = onExit,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(36.dp)
                .glass(CircleShape, GlassEmphasis.Selected)
                .padding(start = 14.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = tr(language, "Terpandu", "Guided"),
                style = MaterialTheme.typography.labelLarge,
                color = colors.onSurface,
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = colors.onSurface,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun GuidedProgress(
    progress: Float,
    stepLabel: String,
    totalLabel: String,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val p by animateFloatAsState(progress.coerceIn(0f, 1f), tween(200), label = "guidedProgress")

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stepLabel,
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
            )
            Text(
                text = totalLabel,
                style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum"),
                color = colors.onSurfaceVariant,
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(CircleShape)
                .background(colors.onSurface.copy(alpha = 0.10f))
        ) {
            Box(
                Modifier
                    .fillMaxWidth(p)
                    .fillMaxHeight()
                    .background(colors.onSurface)
            )
        }
    }
}

@Composable
private fun MessagePane(
    title: String,
    message: String,
    language: String,
    onClose: () -> Unit,
    onRetry: (() -> Unit)?,
) {
    Column(Modifier.fillMaxSize()) {
        PageHeader(language, onClose) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                if (onRetry != null) {
                    TextButton(onClick = onRetry) { Text(tr(language, "Coba lagi", "Retry")) }
                }
            }
        }
    }
}

/* ═══════════════════════════ WORDS ═══════════════════════════ */

/**
- Word preview. Long text is truncated, then "View complete" appears; the entire area can be tapped
- to open the full reader.
 *
- Free     : swipe left/right to change dhikr (favorites first).
- Guided   : cannot be swiped (accidental swipes must not skip steps),
-            dots at the bottom are step indicators only.
 */
@Composable
private fun WordsPane(
    dhikr: Dhikr,
    language: String,
    swipeEnabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onExpand: () -> Unit,
    guidedItem: DhikrItem?,
    guidedEntryIndex: Int,
    position: String?,
    modifier: Modifier,
) {
    val colors = MaterialTheme.colorScheme
    var truncated by remember(dhikr) { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .swipeNavigate(enabled = swipeEnabled, onPrevious = onPrevious, onNext = onNext)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                role = Role.Button,
                onClickLabel = tr(language, "Lihat lengkap", "View in full"),
                onClick = onExpand,
            )
            .padding(horizontal = 28.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Crossfade(targetState = dhikr, label = "words") { d ->
                WordsText(d, language, onOverflow = { if (it) truncated = true })
            }

            if (truncated) {
                Row(
                    modifier = Modifier
                        .height(36.dp)
                        .glass(CircleShape)
                        .padding(start = 14.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = tr(language, "Lihat lengkap", "View in full"),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurface,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = colors.onSurface,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            if (guidedItem != null && guidedItem.entries.size > 1) {
                EntryDots(
                    count = guidedItem.entries.size,
                    selected = guidedEntryIndex,
                    labelFor = { i -> guidedItem.entries[i].dhikr.displayTitle(language) },
                )
            } else if (position != null) {
                Text(
                    text = position,
                    style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum"),
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun WordsText(d: Dhikr, language: String, onOverflow: (Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val arabic = d.arabic
    val title = d.title(language)
    val meaning = d.text(language)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (arabic.isNotEmpty()) {
            Text(
                text = arabic,
                style = if (arabic.length < 60) ArabicType.headlineMedium else ArabicType.headlineSmall,
                color = colors.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { onOverflow(it.hasVisualOverflow) },
            )
        }
        if (!meaning.isNullOrEmpty()) {
            Text(
                text = meaning,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { onOverflow(it.hasVisualOverflow) },
            )
        }
    }
}

@Composable
private fun EntryDots(
    count: Int,
    selected: Int,
    labelFor: (Int) -> String,
) {
    val colors = MaterialTheme.colorScheme

    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(count) { i ->
            val isSelected = i == selected
            val width by animateDpAsState(if (isSelected) 22.dp else 8.dp, tween(150), label = "dot")
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 16.dp, minHeight = 24.dp)
                    .semantics { contentDescription = labelFor(i) },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .width(width)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) colors.onSurface else colors.onSurface.copy(alpha = 0.25f)
                        )
                )
            }
        }
    }
}

@Composable
private fun Modifier.swipeNavigate(
    enabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
): Modifier {
    val previous by rememberUpdatedState(onPrevious)
    val next by rememberUpdatedState(onNext)
    if (!enabled) return this

    return pointerInput(Unit) {
        var total = 0f
        val threshold = 64.dp.toPx()
        detectHorizontalDragGestures(
            onDragStart = { total = 0f },
            onDragEnd = {
                if (total <= -threshold) next() else if (total >= threshold) previous()
            },
            onDragCancel = { total = 0f },
        ) { _, dragAmount -> total += dragAmount }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderSheet(
    dhikrs: List<Dhikr>,
    startIndex: Int,
    selectable: Boolean,
    currentKey: String,
    favorites: Set<String>,
    language: String,
    onUse: (Dhikr) -> Unit,
    onToggleFavorite: (Dhikr) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val pager = rememberPagerState(
        initialPage = startIndex.coerceIn(0, dhikrs.lastIndex),
    ) { dhikrs.size }
    val page = pager.currentPage.coerceIn(0, dhikrs.lastIndex)
    val shown = dhikrs[page]
    val isCurrent = shown.freeKey() == currentKey
    val isFavorite = shown.freeKey() in favorites

    ScrimBottomSheet(
        onDismiss = onDismiss,
        heightFraction = 0.92f,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${page + 1} / ${dhikrs.size}",
                style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum"),
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (selectable) {
                FavoriteButton(
                    favorite = isFavorite,
                    language = language,
                    onClick = { onToggleFavorite(shown) },
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = tr(language, "Tutup", "Close"),
                )
            }
        }

        HorizontalPager(
            state = pager,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { index ->
            ReaderPage(dhikrs[index], language)
        }

        if (selectable) {
            PillButton(
                label = if (isCurrent) {
                    tr(language, "Sedang dipakai", "In use")
                } else {
                    tr(language, "Pakai dzikir ini", "Use this dhikr")
                },
                filled = !isCurrent,
                enabled = !isCurrent,
                onClick = {
                    onUse(shown)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun ReaderPage(d: Dhikr, language: String) {
    val colors = MaterialTheme.colorScheme
    val title = d.displayTitle(language)
    val arabic = d.arabic
    val latin = d.latinText()
    val meaning = d.text(language)
    val reference = d.referenceText()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (arabic.isNotEmpty()) {
            Text(
                text = arabic,
                style = if (arabic.length < 80) ArabicType.headlineMedium else ArabicType.headlineSmall,
                color = colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (latin.isNotEmpty()) {
            Text(
                text = latin,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        if (!meaning.isNullOrEmpty()) {
            Text(
                text = meaning,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurface,
                textAlign = TextAlign.Center,
            )
        }
        if (reference.isNotEmpty()) {
            Text(
                text = reference,
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

/* ═══════════════════════════ COUNTER ═══════════════════════════ */
@Composable
private fun TapCounter(
    count: Int,
    target: Int,
    language: String,
    onTap: () -> Unit,
    modifier: Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val hasTarget = target > 0
    val done = hasTarget && count >= target

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "counterPress",
    )
    val progress = animateFloatAsState(
        targetValue = if (hasTarget) (count.toFloat() / target).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 150),
        label = "counterProgress",
    )
    val track = colors.onSurface.copy(alpha = 0.10f)
    val arc = colors.onSurface

    val label = tr(language, "Penghitung tasbih", "Tasbih counter")
    val addOne = tr(language, "Tambah satu", "Add one")
    val stateText = if (hasTarget) "$count ${tr(language, "dari", "of")} $target" else "$count"

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClickLabel = addOne,
                onClick = onTap,
            )
            .semantics {
                contentDescription = label
                stateDescription = stateText
                liveRegion = LiveRegionMode.Polite
            },
        contentAlignment = Alignment.Center,
    ) {
        val size = minOf(maxWidth - 32.dp, maxHeight - 16.dp, 440.dp).coerceAtLeast(120.dp)
        val stroke = (size.value * 0.022f).coerceIn(5f, 10f).dp
        val countSize = (size.value * 0.30f).coerceIn(56f, 120f).sp

        Box(Modifier.size(size), contentAlignment = Alignment.Center) {
            Canvas(Modifier.matchParentSize()) {
                val strokePx = stroke.toPx()
                val inset = strokePx / 2f
                val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
                drawArc(
                    color = track,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokePx),
                )
                if (progress.value > 0f) {
                    drawArc(
                        color = arc,
                        startAngle = -90f,
                        sweepAngle = 360f * progress.value,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .size(size * 0.9f)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .glass(
                        shape = CircleShape,
                        emphasis = if (done) GlassEmphasis.Selected else GlassEmphasis.Regular,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = countSize,
                        lineHeight = countSize * 1.1f,
                        fontWeight = FontWeight.Light,
                        fontFeatureSettings = "tnum",
                    ),
                    color = colors.onSurface,
                )
                if (hasTarget) {
                    Text(
                        text = "${tr(language, "dari", "of")} $target",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

/* ═══════════════════════════ DOCK ═══════════════════════════ */

/**
- Essential controls, within thumb reach.
- Free    : select target, reset.
- Guided  : previous, reset, skip / finish.
 */
@Composable
private fun ControlDock(
    state: TasbihUiState,
    language: String,
    actions: Actions,
    modifier: Modifier,
) {
    Row(
        modifier = modifier
            .widthIn(max = 560.dp)
            .fillMaxWidth()
            .glass(RoundedCornerShape(28.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.autoRun) {
            DockAction(
                label = tr(language, "Sebelumnya", "Back"),
                enabled = state.entryIndex > 0,
                onClick = actions.onPrevious,
                modifier = Modifier.weight(1f),
            )
            ResetControl(state.confirmingReset, state.count > 0, language, actions.onReset)
            DockAction(
                label = if (state.isLastEntry) tr(language, "Selesai", "Finish")
                else tr(language, "Lewati", "Skip"),
                enabled = true,
                onClick = actions.onNext,
                modifier = Modifier.weight(1f),
            )
        } else {
            SegmentedPicker(
                labels = Targets.map { if (it == 0) tr(language, "Bebas", "Free") else "$it" },
                selectedIndex = Targets.indexOf(state.freeTarget),
                onSelect = { i -> actions.onSelectTarget(Targets[i]) },
                modifier = Modifier.weight(1f),
            )
            ResetControl(state.confirmingReset, state.count > 0, language, actions.onReset)
        }
    }
}

@Composable
private fun DockAction(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(CircleShape)
            .background(colors.onSurface.copy(alpha = 0.06f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) colors.onSurface else colors.onSurfaceVariant.copy(alpha = 0.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ResetControl(
    confirming: Boolean,
    enabled: Boolean,
    language: String,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val content = if (enabled) colors.onSurface else colors.onSurfaceVariant.copy(alpha = 0.5f)

    Row(
        modifier = Modifier
            .animateContentSize()
            .defaultMinSize(minWidth = 48.dp)
            .height(48.dp)
            .clip(CircleShape)
            .background(colors.onSurface.copy(alpha = if (confirming) 0.14f else 0.06f))
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClickLabel = tr(language, "Reset hitungan", "Reset count"),
                onClick = onClick,
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(20.dp),
        )
        if (confirming) {
            Text(
                text = tr(language, "Ketuk lagi", "Tap again"),
                style = MaterialTheme.typography.labelLarge,
                color = content,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SegmentedPicker(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    BoxWithConstraints(
        modifier = modifier
            .height(48.dp)
            .clip(CircleShape)
            .background(colors.onSurface.copy(alpha = 0.06f))
            .padding(2.dp)
            .selectableGroup(),
    ) {
        val segmentWidth = maxWidth / labels.size
        val thumbX by animateDpAsState(
            targetValue = segmentWidth * selectedIndex.coerceAtLeast(0),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
            label = "segmentThumb",
        )

        if (selectedIndex >= 0) {
            Box(
                Modifier
                    .offset(x = thumbX)
                    .width(segmentWidth)
                    .fillMaxHeight()
                    .glass(CircleShape, GlassEmphasis.Selected)
            )
        }

        Row(Modifier.fillMaxSize()) {
            labels.forEachIndexed { i, label ->
                val isSelected = i == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .selectable(
                            selected = isSelected,
                            role = Role.RadioButton,
                            onClick = { onSelect(i) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) colors.onSurface else colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/* ═══════════════════════════ LIBRARY ═══════════════════════════ */

/**
- Free    : all single dhikr, choose any. Favorites pinned at the top.
- Guided  : complete sequence that runs automatically. Favorites also at the top.
- Same content becomes a sheet on phones and a persistent rail on tablets.
 */
@Composable
private fun LibraryContent(
    state: TasbihUiState,
    language: String,
    onPickFree: (String) -> Unit,
    onPickGuided: (Int) -> Unit,
    onToggleFavFree: (String) -> Unit,
    onToggleFavGuided: (String) -> Unit,
    modifier: Modifier,
) {
    val colors = MaterialTheme.colorScheme
    var tab by remember { mutableIntStateOf(if (state.autoRun) 1 else 0) }
    val favLabel = tr(language, "Sematkan / lepas favorit", "Pin / unpin favorite")

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = tr(language, "Dzikir", "Dhikr"),
            style = MaterialTheme.typography.titleLarge,
            color = colors.onSurface,
        )

        SegmentedPicker(
            labels = listOf(tr(language, "Bebas", "Free"), tr(language, "Terpandu", "Guided")),
            selectedIndex = tab,
            onSelect = { tab = it },
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = if (tab == 0) {
                tr(
                    language,
                    "Pilih dzikir apa saja dan hitung sebebasnya, tanpa urutan.",
                    "Pick any dhikr and count freely, in no particular order.",
                )
            } else {
                tr(
                    language,
                    "Menjalani semua langkah dan berpindah sendiri.",
                    "Runs through every step and moves on by itself.",
                )
            },
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
        )

        if (tab == 0) {
            val currentKey = state.currentFree?.freeKey()
            val (pinned, others) = state.freeList.partition { it.freeKey() in state.favoriteFree }

            @Composable
            fun FreeRow(d: Dhikr) {
                val key = d.freeKey()
                LibraryRow(
                    title = d.displayTitle(language),
                    subtitle = d.text(language).takeUnless { it.isNullOrEmpty() } ?: d.arabic,
                    badge = null,
                    selected = !state.autoRun && key == currentKey,
                    favorite = key in state.favoriteFree,
                    favLabel = favLabel,
                    onClick = { onPickFree(key) },
                    onToggleFavorite = { onToggleFavFree(key) },
                )
            }

            if (pinned.isNotEmpty()) {
                SectionLabel(tr(language, "Disematkan", "Pinned"))
                pinned.forEach { FreeRow(it) }
                SectionLabel(tr(language, "Semua dzikir", "All dhikr"))
            }
            others.forEach { FreeRow(it) }
        } else {
            val indexed = state.items.withIndex().toList()
            val (pinned, others) = indexed.partition { it.value.guidedKey() in state.favoriteGuided }

            @Composable
            fun GuidedRow(index: Int, item: DhikrItem) {
                LibraryRow(
                    title = item.name(language),
                    subtitle = item.entries.joinToString(" · ") {
                        "${it.dhikr.displayTitle(language)} ×${it.repetitions}"
                    },
                    badge = "${item.entries.sumOf { it.repetitions }}×",
                    selected = state.autoRun && index == state.selectedItemIndex,
                    favorite = item.guidedKey() in state.favoriteGuided,
                    favLabel = favLabel,
                    onClick = { onPickGuided(index) },
                    onToggleFavorite = { onToggleFavGuided(item.guidedKey()) },
                )
            }

            if (pinned.isNotEmpty()) {
                SectionLabel(tr(language, "Disematkan", "Pinned"))
                pinned.forEach { GuidedRow(it.index, it.value) }
                SectionLabel(tr(language, "Semua rangkaian", "All routines"))
            }
            others.forEach { GuidedRow(it.index, it.value) }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun LibraryRow(
    title: String,
    subtitle: String,
    badge: String?,
    selected: Boolean,
    favorite: Boolean,
    favLabel: String,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .glass(
                shape = RoundedCornerShape(16.dp),
                emphasis = if (selected) GlassEmphasis.Selected else GlassEmphasis.Regular,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = colors.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (badge != null) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum"),
                    color = colors.onSurfaceVariant,
                )
            }
            // Tanda centang, bukan warna, menandai yang dipilih.
            Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = colors.onSurface,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = favLabel,
                tint = if (favorite) colors.onSurface else colors.onSurfaceVariant,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

/* ═══════════════════════════ FINISH ═══════════════════════════ */

/**
- Replaces the entire page content when the guided sequence is finished. In-place, not a dialog.
- Appears once with a slow fade (non-repeating), calm, without scores or rewards.
- "Finish" returns to free mode, "Restart" starts the same sequence from the beginning.
 */
@Composable
private fun FinishPanel(
    item: DhikrItem,
    language: String,
    onRepeat: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier,
) {
    val colors = MaterialTheme.colorScheme
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val appear by animateFloatAsState(if (shown) 1f else 0f, tween(400), label = "finishIn")

    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .verticalScroll(rememberScrollState())
                .graphicsLayer {
                    alpha = appear
                    translationY = (1f - appear) * 24.dp.toPx()
                }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .glass(CircleShape, GlassEmphasis.Selected),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = colors.onSurface,
                    modifier = Modifier.size(36.dp),
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = tr(language, "Alhamdulillah, selesai", "Alhamdulillah, completed"),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "${item.name(language)} · ${item.entries.sumOf { it.repetitions }}×",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glass(RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item.entries.forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = entry.dhikr.displayTitle(language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "×${entry.repetitions}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
                            color = colors.onSurfaceVariant,
                        )
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PillButton(
                    label = tr(language, "Ulangi", "Repeat"),
                    filled = false,
                    onClick = onRepeat,
                    modifier = Modifier.weight(1f),
                )
                PillButton(
                    label = tr(language, "Selesai", "Done"),
                    filled = true,
                    onClick = onDone,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PillButton(
    label: String,
    filled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .height(52.dp)
            .then(
                if (filled) Modifier.clip(CircleShape).background(colors.onSurface)
                else Modifier.glass(CircleShape)
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = when {
                !enabled -> colors.onSurfaceVariant
                filled -> colors.background
                else -> colors.onSurface
            },
        )
    }
}

@Composable
private fun HapticFeedbackDialog(
    current: HapticMode,
    language: String,
    onSelect: (HapticMode) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    ScrimDialog(onDismiss = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colors.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = tr(language, "Umpan balik sentuhan", "Touch feedback"),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                )
                Text(
                    text = tr(
                        language,
                        "Pilih bagaimana perangkat merespons setiap ketukan.",
                        "Choose how your device responds to each tap.",
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                FeedbackRow(
                    title = tr(language, "Haptik", "Haptic"),
                    subtitle = tr(
                        language,
                        "Getaran halus bawaan sistem. Menghormati pengaturan sentuhan Anda.",
                        "Subtle system-tuned vibration. Respects your touch-feedback settings.",
                    ),
                    selected = current == HapticMode.Haptic,
                    onClick = { onSelect(HapticMode.Haptic) },
                )
                FeedbackRow(
                    title = tr(language, "Getaran", "Vibration"),
                    subtitle = tr(
                        language,
                        "Getaran langsung. Tetap terasa meski haptik sistem dimatikan.",
                        "Direct motor vibration. Fires even when system haptics is off.",
                    ),
                    selected = current == HapticMode.Vibration,
                    onClick = { onSelect(HapticMode.Vibration) },
                )
                FeedbackRow(
                    title = tr(language, "Senyap", "Silent"),
                    subtitle = tr(
                        language,
                        "Tanpa umpan balik sentuhan.",
                        "No tactile feedback.",
                    ),
                    selected = current == HapticMode.Silent,
                    onClick = { onSelect(HapticMode.Silent) },
                )
            }
        }
    }
}

@Composable
private fun FeedbackRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }
    }
}

// ── Dispatcher helpers ─────────────────────────────────────────

private fun TasbihHaptic.hapticFeedbackType(): HapticFeedbackType = when (this) {
    TasbihHaptic.Tap, TasbihHaptic.ResetConfirmNeeded -> HapticFeedbackType.TextHandleMove
    TasbihHaptic.TargetReached, TasbihHaptic.ResetConfirmed,
    TasbihHaptic.StepDone, TasbihHaptic.RoutineFinished -> HapticFeedbackType.LongPress
}

private fun Context.vibratorOrNull(): Vibrator? = try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
} catch (_: Exception) { null }

private fun Vibrator.vibrateCompat(event: TasbihHaptic) {
    val ms: Long = when (event) {
        TasbihHaptic.Tap -> 12L
        TasbihHaptic.ResetConfirmNeeded -> 20L
        TasbihHaptic.TargetReached, TasbihHaptic.ResetConfirmed,
        TasbihHaptic.StepDone, TasbihHaptic.RoutineFinished -> 35L
    }
    val amplitude: Int = when (event) {
        TasbihHaptic.Tap -> 80
        TasbihHaptic.ResetConfirmNeeded -> 130
        else -> 200
    }
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrate(VibrationEffect.createOneShot(ms, amplitude))
        } else {
            @Suppress("DEPRECATION")
            vibrate(ms)
        }
    } catch (_: Exception) { /* DND / ringer silent may reject; fine */ }
}

private fun tr(language: String, id: String, en: String) = if (language == "id") id else en