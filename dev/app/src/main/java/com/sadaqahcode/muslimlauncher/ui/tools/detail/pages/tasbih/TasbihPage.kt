package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.domain.model.tools.name
import com.sadaqahcode.muslimlauncher.domain.model.tools.text
import com.sadaqahcode.muslimlauncher.domain.model.tools.title
import com.sadaqahcode.muslimlauncher.ui.theme.ArabicType
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.ToolPageDefaults
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.ToolTopAppBar
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components.CounterDisc
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components.DhikrChipRow
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components.DhikrPickerSheet
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components.ResetButton
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components.TargetRow
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TasbihPage(
    tool: Tool,
    onClose: () -> Unit,
    viewModel: TasbihViewModel = hiltViewModel(),
) {
    val colors = MaterialTheme.colorScheme
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    var showPicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(viewModel) {
        viewModel.haptics.collect { event ->
            val type = when (event) {
                TasbihHaptic.Tap -> HapticFeedbackType.TextHandleMove
                TasbihHaptic.TargetReached -> HapticFeedbackType.LongPress
                TasbihHaptic.ResetConfirmNeeded -> HapticFeedbackType.TextHandleMove
                TasbihHaptic.ResetConfirmed -> HapticFeedbackType.LongPress
            }
            haptic.performHapticFeedback(type)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colors.background,
        contentColor = colors.onBackground,
        topBar = {
            ToolTopAppBar(
                title = stringResource(tool.titleRes),
                onClose = onClose,
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { showPicker = true }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Choose dhikr",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        TasbihContent(
            state = state,
            contentPadding = innerPadding,
            onTap = viewModel::tap,
            onReset = viewModel::requestReset,
            onSelectItem = viewModel::selectItem,
            onSelectTarget = viewModel::selectTarget,
            onRetry = { viewModel.load(tryOnlineFirst = true) },
        )
    }

    if (showPicker && state.items.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { showPicker = false },
            sheetState = sheetState,
        ) {
            DhikrPickerSheet(
                items = state.items,
                selectedIndex = state.selectedItemIndex,
                language = LocalLocale.current.platformLocale.language,
                onSelect = { index ->
                    viewModel.selectItem(index)
                    showPicker = false
                },
            )
        }
    }
}

@Composable
private fun TasbihContent(
    state: TasbihUiState,
    contentPadding: PaddingValues,
    onTap: () -> Unit,
    onReset: () -> Unit,
    onSelectItem: (Int) -> Unit,
    onSelectTarget: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(ToolPageDefaults.contentPadding(contentPadding)),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.items.isEmpty() && state.isLoading -> CircularProgressIndicator()

            state.items.isEmpty() && state.error != null -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = state.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                TextButton(onClick = onRetry) {
                    Text(text = "Retry")
                }
            }

            state.items.isEmpty() -> Text(
                text = "No dhikr available.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            else -> TasbihBody(
                state = state,
                onTap = onTap,
                onReset = onReset,
                onSelectItem = onSelectItem,
                onSelectTarget = onSelectTarget,
            )
        }
    }
}

@Composable
private fun TasbihBody(
    state: TasbihUiState,
    onTap: () -> Unit,
    onReset: () -> Unit,
    onSelectItem: (Int) -> Unit,
    onSelectTarget: (Int) -> Unit,
) {
    val language = LocalLocale.current.platformLocale.language
    val labels = state.items.map { it.name(language) }

    val dhikr = state.currentDhikr
    val arabic = dhikr?.arabic.orEmpty()
    val title = dhikr?.title(language).orEmpty()
    val meaning = dhikr?.text(language).orEmpty()

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val landscape = maxWidth > maxHeight && maxHeight < 520.dp
        val compact = maxHeight < 600.dp
        val dense = maxHeight < 460.dp

        if (landscape) {
            TasbihLandscape(
                arabic = arabic,
                title = title,
                meaning = meaning,
                labels = labels,
                selectedIndex = state.selectedItemIndex,
                count = state.count,
                target = state.target,
                confirmingReset = state.confirmingReset,
                onSelectItem = onSelectItem,
                onSelectTarget = onSelectTarget,
                onTap = onTap,
                onReset = onReset,
            )
        } else {
            TasbihPortrait(
                arabic = arabic,
                title = title,
                meaning = meaning,
                labels = labels,
                selectedIndex = state.selectedItemIndex,
                count = state.count,
                target = state.target,
                confirmingReset = state.confirmingReset,
                compact = compact,
                dense = dense,
                onSelectItem = onSelectItem,
                onSelectTarget = onSelectTarget,
                onTap = onTap,
                onReset = onReset,
            )
        }
    }
}

@Composable
private fun TasbihPortrait(
    arabic: String,
    title: String,
    meaning: String,
    labels: List<String>,
    selectedIndex: Int,
    count: Int,
    target: Int,
    confirmingReset: Boolean,
    compact: Boolean,
    dense: Boolean,
    onSelectItem: (Int) -> Unit,
    onSelectTarget: (Int) -> Unit,
    onTap: () -> Unit,
    onReset: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DhikrChipRow(
            labels = labels,
            selectedIndex = selectedIndex,
            onSelect = onSelectItem,
            compact = compact,
        )

        Spacer(Modifier.height(if (compact) 12.dp else 20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (arabic.isNotEmpty()) {
                Text(
                    text = arabic,
                    style = if (dense) ArabicType.headlineSmall else ArabicType.headlineMedium,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }
            if (!dense) {
                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        style = if (compact) MaterialTheme.typography.titleMedium
                        else MaterialTheme.typography.titleLarge,
                        color = colors.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (meaning.isNotEmpty()) {
                    Text(
                        text = meaning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            BoxWithConstraints {
                CounterDisc(
                    count = count,
                    target = target,
                    size = minOf(maxWidth, maxHeight, 360.dp),
                    onTap = onTap,
                )
            }
        }

        Spacer(Modifier.height(if (compact) 4.dp else 12.dp))
        TargetRow(
            target = target,
            compact = compact,
            onSelectTarget = onSelectTarget,
        )

        Spacer(Modifier.height(if (compact) 4.dp else 8.dp))
        ResetButton(
            confirmingReset = confirmingReset,
            enabled = count > 0,
            onClick = onReset,
        )
    }
}

@Composable
private fun TasbihLandscape(
    arabic: String,
    title: String,
    meaning: String,
    labels: List<String>,
    selectedIndex: Int,
    count: Int,
    target: Int,
    confirmingReset: Boolean,
    onSelectItem: (Int) -> Unit,
    onSelectTarget: (Int) -> Unit,
    onTap: () -> Unit,
    onReset: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            DhikrChipRow(
                labels = labels,
                selectedIndex = selectedIndex,
                onSelect = onSelectItem,
                compact = true,
            )

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (arabic.isNotEmpty()) {
                    Text(
                        text = arabic,
                        style = ArabicType.headlineSmall,
                        color = colors.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (meaning.isNotEmpty()) {
                    Text(
                        text = meaning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                BoxWithConstraints {
                    CounterDisc(
                        count = count,
                        target = target,
                        size = minOf(maxWidth, maxHeight),
                        onTap = onTap,
                    )
                }
            }

            TargetRow(
                target = target,
                compact = true,
                onSelectTarget = onSelectTarget,
            )
            Spacer(Modifier.height(4.dp))
            ResetButton(
                confirmingReset = confirmingReset,
                enabled = count > 0,
                onClick = onReset,
            )
        }
    }
}