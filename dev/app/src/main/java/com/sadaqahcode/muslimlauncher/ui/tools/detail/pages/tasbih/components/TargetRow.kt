package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** 0 = no target (count freely). */
private val TargetPresets = listOf(33, 99, 100, 0)

@Composable
internal fun TargetRow(
    target: Int,
    compact: Boolean,
    onSelectTarget: (Int) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .selectableGroup()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Target",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp),
            )
            TargetPresets.forEach { t ->
                ChoiceChip(
                    label = when {
                        t != 0 -> "$t"
                        compact -> "∞"
                        else -> "None"
                    },
                    selected = t == target,
                    onClick = { onSelectTarget(t) },
                    compact = compact,
                )
            }
        }
    }
}