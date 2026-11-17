package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors

@Composable
internal fun EmptyPinnedHint(textScale: AppTextSize) {
    val lc = LocalLegibleColors.current

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            "Nothing pinned, on purpose.",
            style = textScale.scaled(MaterialTheme.typography.titleMedium),
            color = lc.scheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Swipe up for all apps.\nPress and hold one to pin it.",
            style = textScale.scaled(MaterialTheme.typography.bodySmall),
            color = lc.scheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
        )
    }
}