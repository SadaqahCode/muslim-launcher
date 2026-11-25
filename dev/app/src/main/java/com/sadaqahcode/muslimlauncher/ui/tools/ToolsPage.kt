package com.sadaqahcode.muslimlauncher.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadaqahcode.muslimlauncher.data.local.tools.toolIcon
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.domain.model.ToolId
import com.sadaqahcode.muslimlauncher.domain.model.ToolRegistry
import com.sadaqahcode.muslimlauncher.domain.model.resolveTools
import com.sadaqahcode.muslimlauncher.ui.common.layout.AppBarPage
import com.sadaqahcode.muslimlauncher.ui.common.layout.PageMaxWidthScreen
import com.sadaqahcode.muslimlauncher.ui.common.layout.ThreeColumnMinWidth
import com.sadaqahcode.muslimlauncher.ui.common.layout.rememberSoftScrollbarSettings
import com.sadaqahcode.muslimlauncher.ui.home.helper.doubleTapGesture
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.tools.components.ReorderToolsList
import com.sadaqahcode.muslimlauncher.ui.tools.components.ToolCard
import kotlinx.coroutines.launch
import my.nanihadesuka.compose.LazyVerticalGridScrollbar

@Composable
fun ToolsPage(
    textScale: AppTextSize,
    paddingTop: Dp,
    useCollapsingBar: Boolean,
    paddingTopEnabled: Boolean,
    onOpenTool: (ToolId) -> Unit,
    onDoubleTapHome: () -> Unit,
    onBack: () -> Unit,
    pageActive: Boolean = true,
    viewModel: ToolsViewModel = hiltViewModel()
) {
    val prefs by viewModel.prefs.collectAsStateWithLifecycle()
    val today by viewModel.today.collectAsStateWithLifecycle()

    val softScrollbar = rememberSoftScrollbarSettings()

    var isReorderMode by remember { mutableStateOf(false) }
    var latestDraftOrder by remember { mutableStateOf<List<ToolId>?>(null) }
    var latestDraftEnabled by remember { mutableStateOf<Map<ToolId, Boolean>?>(null) }

    val resolved = remember(prefs, today) { resolveTools(prefs, today) }
    val banner = resolved.banner
    val tools = resolved.tools

    val allTools = remember(prefs) {
        (prefs.order + ToolRegistry.defaultOrder)
            .distinct()
            .mapNotNull { ToolRegistry.byId[it] }
    }

    val gridState = rememberLazyGridState()

    fun saveAndClose() {
        val order = latestDraftOrder ?: allTools.map { it.id }
        val enabled = latestDraftEnabled ?: allTools.associate { it.id to prefs.isEnabled(it.id) }
        viewModel.setOrder(order)
        enabled.forEach { (id, isEnabled) -> viewModel.setEnabled(id, isEnabled) }
        isReorderMode = false
    }

    fun enterReorder() {
        latestDraftOrder = allTools.map { it.id }
        latestDraftEnabled = allTools.associate { it.id to prefs.isEnabled(it.id) }
        isReorderMode = true
    }

    LaunchedEffect(pageActive) {
        if (!pageActive) {
            if (isReorderMode) {
                saveAndClose()
            }
            launch { gridState.scrollToItem(0) }
        }
    }

    BackHandler(enabled = isReorderMode) {
        saveAndClose()
    }

    if (isReorderMode) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Box(Modifier.widthIn(max = PageMaxWidthScreen).fillMaxSize()) {
                ReorderToolsList(
                    textScale = textScale,
                    tools = allTools,
                    prefs = prefs,
                    onDraftChanged = { order, enabled ->
                        latestDraftOrder = order
                        latestDraftEnabled = enabled
                    },
                    onDone = { orderedIds, enabled ->
                        latestDraftOrder = orderedIds
                        latestDraftEnabled = enabled
                        saveAndClose()
                    },
                )
            }
        }
        return
    }

    val lc = LocalLegibleColors.current

    AppBarPage(
        title = {
            Text(
                text = "Tools",
                style = textScale.scaled(MaterialTheme.typography.titleLarge),
                fontWeight = FontWeight.Medium,
                color = lc.scheme.onBackground,
            )
        },
        subtitle = {
            Text(
                text = "Daily Islamic essentials",
                style = textScale.scaled(MaterialTheme.typography.bodySmall),
                color = lc.scheme.onBackground.copy(alpha = 0.72f),
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                    contentDescription = "Back to home",
                    tint = lc.scheme.onBackground,
                )
            }
        },
        actions = {
            IconButton(onClick = { enterReorder() }) {
                Icon(
                    imageVector = Icons.Outlined.UnfoldMore,
                    contentDescription = "Manage tools",
                    tint = lc.scheme.onBackground,
                )
            }
        },
        useCollapsingBar = useCollapsingBar,
        paddingTop = paddingTop,
        paddingTopEnabled = paddingTopEnabled,
        wrapperModifier = Modifier.doubleTapGesture(
            onDoubleTap = onDoubleTapHome,
            isGatingOk = { true },
        ),
    ) { innerPadding ->
        BoxWithConstraints(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val columns = if (maxWidth >= ThreeColumnMinWidth) 3 else 2
            LazyVerticalGridScrollbar(state = gridState, settings = softScrollbar) {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 28.dp,
                        end = 28.dp,
                        top = 8.dp,
                        bottom = 38.dp,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    banner?.let { b ->
                        item(
                            key = "banner-${b.id.key}",
                            span = { GridItemSpan(maxLineSpan) },
                        ) {
                            ToolCard(
                                icon = toolIcon(b.id),
                                title = stringResource(b.titleRes),
                                liveValue = stringResource(b.subtitleRes),
                                context = "",
                                onClick = { onOpenTool(b.id) },
                            )
                        }
                    }
                    items(tools, key = { it.id.key }) { tool ->
                        ToolCard(
                            icon = toolIcon(tool.id),
                            title = stringResource(tool.titleRes),
                            liveValue = stringResource(tool.subtitleRes),
                            context = "",
                            onClick = { onOpenTool(tool.id) },
                        )
                    }
                }
            }
        }
    }
}