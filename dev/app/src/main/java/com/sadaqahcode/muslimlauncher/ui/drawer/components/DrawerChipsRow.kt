package com.sadaqahcode.muslimlauncher.ui.drawer.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.AllAppsIcon
import com.sadaqahcode.muslimlauncher.domain.model.AppCategory
import com.sadaqahcode.muslimlauncher.domain.model.DrawerChip
import com.sadaqahcode.muslimlauncher.domain.model.chipLabel
import com.sadaqahcode.muslimlauncher.domain.model.icon
import com.sadaqahcode.muslimlauncher.ui.theme.GlassEmphasis
import com.sadaqahcode.muslimlauncher.ui.theme.glass

@Composable
internal fun DrawerChipsRow(
    chips: List<DrawerChip>,
    selected: AppCategory?,
    onSelect: (AppCategory?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    var viewportWidthPx by remember { mutableIntStateOf(0) }
    var rowStartPx by remember { mutableIntStateOf(-1) }

    val chipCenters = remember { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(chips) {
        chipCenters.clear()
        scrollState.scrollTo(0)
    }

    // Keep the selected chip centred in view.
    LaunchedEffect(selected, viewportWidthPx, chipCenters.size) {
        if (viewportWidthPx == 0) return@LaunchedEffect
        if (chipCenters.size != chips.size) return@LaunchedEffect
        val idx = chips.indexOfFirst { it.filter == selected }
        if (idx < 0) return@LaunchedEffect
        val centerX = chipCenters[idx] ?: return@LaunchedEffect
        val target = (centerX - viewportWidthPx / 2)
            .coerceIn(0, scrollState.maxValue)
        scrollState.animateScrollTo(target)
    }

    BoxWithConstraints(modifier) {
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
            chips.forEachIndexed { index, chip ->
                Box(
                    modifier = Modifier.onGloballyPositioned { coords ->
                        if (index !in chipCenters && rowStartPx >= 0) {
                            // Content X = where the chip would sit if scrollState were 0.
                            // At layout time, scrollState may already be > 0 (e.g. when
                            // recomposing after a scroll), so add it back.
                            val contentX = coords.positionInRoot().x.toInt() -
                                    rowStartPx + scrollState.value
                            chipCenters[index] = contentX + coords.size.width / 2
                        }
                    }
                ) {
                    CategoryChip(
                        icon = chip.filter?.icon ?: AllAppsIcon,
                        label = chip.filter?.chipLabel() ?: "All",
                        count = chip.count,
                        selected = chip.filter == selected,
                        onClick = { onSelect(chip.filter) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    icon: ImageVector,
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val content = if (selected) colors.onSurface else colors.onSurfaceVariant

    Box(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(40.dp)
                .glass(
                    shape = CircleShape,
                    emphasis = if (selected) GlassEmphasis.Selected else GlassEmphasis.Regular,
                )
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = content,
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum"),
                color = content.copy(alpha = 0.65f),
            )
        }
    }
}