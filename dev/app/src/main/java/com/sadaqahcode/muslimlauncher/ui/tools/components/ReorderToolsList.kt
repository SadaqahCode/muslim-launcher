package com.sadaqahcode.muslimlauncher.ui.tools.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.domain.model.ToolId
import com.sadaqahcode.muslimlauncher.domain.model.ToolPreferences
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors

@Composable
fun ReorderToolsList(
    textScale: AppTextSize,
    tools: List<Tool>,
    prefs: ToolPreferences,
    onDraftChanged: (List<ToolId>, Map<ToolId, Boolean>) -> Unit,
    onDone: (List<ToolId>, Map<ToolId, Boolean>) -> Unit,
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val lc = LocalLegibleColors.current

    // Single source of truth
    val localOrder = remember(tools) { mutableStateListOf(*tools.toTypedArray()) }
    var localEnabled by remember(tools, prefs) {
        mutableStateOf(tools.associate { it.id to prefs.isEnabled(it.id) })
    }

    // Initial draft, so parent has correct value even before any edit
    LaunchedEffect(Unit) {
        onDraftChanged(localOrder.map { it.id }, localEnabled)
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding()
                .padding(start = 28.dp, end = 16.dp, top = if (isLandscape) 4.dp else 16.dp, bottom = if (isLandscape) 8.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Reorder",
                    style = textScale.scaled(MaterialTheme.typography.titleLarge),
                    fontWeight = FontWeight.Medium,
                    color = lc.scheme.onBackground,
                )
                Text(
                    text = "Use arrows to reorder · toggle to enable / disable",
                    style = textScale.scaled(MaterialTheme.typography.bodySmall),
                    color = lc.scheme.onBackground.copy(alpha = 0.72f),
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            TextButton(
                onClick = {
                    onDone(localOrder.map { it.id }, localEnabled)
                },
            ) {
                Text(
                    text = "Done",
                    color = lc.scheme.primary,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .padding(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            for (index in localOrder.indices) {
                val tool = localOrder[index]
                key("t-${tool.id.key}") {
                    val isFirst = index == 0
                    val isLast = index == localOrder.lastIndex
                    val checked = localEnabled[tool.id] ?: true
                    ReorderToolsRow(
                        onMoveUp = {
                            if (!isFirst) {
                                val tmp = localOrder[index]
                                localOrder[index] = localOrder[index - 1]
                                localOrder[index - 1] = tmp
                                onDraftChanged(localOrder.map { item -> item.id }, localEnabled)
                            }
                        },
                        onMoveDown = {
                            if (!isLast) {
                                val tmp = localOrder[index]
                                localOrder[index] = localOrder[index + 1]
                                localOrder[index + 1] = tmp
                                onDraftChanged(localOrder.map { item -> item.id }, localEnabled)
                            }
                        },
                        canMoveUp = !isFirst,
                        canMoveDown = !isLast,
                        checked = checked,
                        onToggle = { isChecked ->
                            val newEnabled = localEnabled + (tool.id to isChecked)
                            localEnabled = newEnabled
                            onDraftChanged(localOrder.map { item -> item.id }, newEnabled)
                        },
                    ) {
                        ToolReorderItem(
                            tool = tool,
                            textScale = textScale,
                        )
                    }
                }
            }
        }
    }
}