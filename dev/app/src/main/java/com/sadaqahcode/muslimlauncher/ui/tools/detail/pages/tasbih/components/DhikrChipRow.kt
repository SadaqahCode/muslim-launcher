package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp

@Composable
internal fun DhikrChipRow(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    compact: Boolean,
) {
    val scrollState = rememberScrollState()
    var viewportWidthPx by remember { mutableIntStateOf(0) }
    var rowStartPx by remember { mutableIntStateOf(-1) }
    val chipCenters = remember { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(labels) {
        chipCenters.clear()
        scrollState.scrollTo(0)
    }

    LaunchedEffect(selectedIndex, viewportWidthPx, chipCenters.size, labels.size) {
        if (viewportWidthPx == 0) return@LaunchedEffect
        if (chipCenters.size != labels.size) return@LaunchedEffect
        val centerX = chipCenters[selectedIndex] ?: return@LaunchedEffect
        val target = (centerX - viewportWidthPx / 2)
            .coerceIn(0, scrollState.maxValue)
        scrollState.animateScrollTo(target)
    }

    BoxWithConstraints(Modifier.fillMaxWidth()) {
        viewportWidthPx = constraints.maxWidth

        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .selectableGroup()
                .onGloballyPositioned {
                    rowStartPx = it.positionInRoot().x.toInt()
                },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            labels.forEachIndexed { index, label ->
                Box(
                    modifier = Modifier.onGloballyPositioned { coords ->
                        if (index !in chipCenters && rowStartPx >= 0) {
                            val contentX = coords.positionInRoot().x.toInt() -
                                    rowStartPx + scrollState.value
                            chipCenters[index] = contentX + coords.size.width / 2
                        }
                    }
                ) {
                    ChoiceChip(
                        label = label,
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) },
                        compact = compact,
                    )
                }
            }
        }
    }
}