package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun DockAction(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    scheme: ColorScheme,
    modifier: Modifier = Modifier,
) {
    val defaultColors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(CircleShape)
            .background(if (enabled) defaultColors.onSurface.copy(alpha = 0.06f) else scheme.surfaceVariant.copy(alpha = 0.06f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) defaultColors.onSurface else scheme.outline.copy(alpha = 0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}