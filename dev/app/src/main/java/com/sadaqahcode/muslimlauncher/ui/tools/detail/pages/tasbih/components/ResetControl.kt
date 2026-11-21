package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr


@Composable
fun ResetControl(
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