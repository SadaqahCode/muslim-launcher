package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.tools.detail.ToolPageCard
import com.sadaqahcode.muslimlauncher.ui.tools.detail.ToolPageDefaults


@Composable
fun NotFoundPage(contentPadding: PaddingValues) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(ToolPageDefaults.contentPadding(contentPadding)),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        ToolPageCard {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "This tool is not available.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurface,
                )
                Text(
                    text = "It may have been removed or renamed in this version.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}