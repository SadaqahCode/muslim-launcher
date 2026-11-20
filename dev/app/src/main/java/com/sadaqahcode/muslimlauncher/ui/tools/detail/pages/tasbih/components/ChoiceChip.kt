package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.theme.GlassEmphasis
import com.sadaqahcode.muslimlauncher.ui.theme.glass

@Composable
internal fun ChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    compact: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val content = if (selected) colors.onSurface else colors.onSurfaceVariant
    val ring by animateColorAsState(
        targetValue = if (selected) colors.onSurface.copy(alpha = 0.7f) else Color.Transparent,
        animationSpec = tween(durationMillis = 150),
        label = "chipRing",
    )

    Box(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .height(if (compact) 34.dp else 40.dp)
                .glass(
                    shape = CircleShape,
                    emphasis = if (selected) GlassEmphasis.Selected else GlassEmphasis.Regular,
                )
                .border(width = 1.5.dp, color = ring, shape = CircleShape)
                .padding(horizontal = if (compact) 12.dp else 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = if (compact) MaterialTheme.typography.labelMedium
                else MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = content,
            )
        }
    }
}