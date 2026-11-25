package com.sadaqahcode.muslimlauncher.ui.tools.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors

@Composable
internal fun ToolReorderItem(
    tool: Tool,
    textScale: AppTextSize,
) {
    val lc = LocalLegibleColors.current
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(lc.scheme.background.copy(alpha = 0.80f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(tool.titleRes),
                style = textScale.scaled(MaterialTheme.typography.bodyMedium),
                color = lc.scheme.onPrimaryContainer,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(tool.subtitleRes),
                style = textScale.scaled(MaterialTheme.typography.bodySmall),
                color = lc.scheme.onPrimaryContainer.copy(alpha = 0.72f),
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}