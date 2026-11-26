package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.tr

@Composable
fun HeaderQibla(
    tool: Tool,
    language: String,
    onClose: () -> Unit,
    onOpenGuide: (() -> Unit)? = null,
    onOpenPermissionStatus: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // left
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = tr(language, "Kembali", "Back"),
            )
        }
        Text(
            text = stringResource(tool.titleRes),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
        )

        // right
        if (onOpenPermissionStatus != null) {
            IconButton(onClick = onOpenPermissionStatus) {
                Icon(
                    imageVector = Icons.Outlined.MyLocation,
                    contentDescription = tr(language, "Status Izin Lokasi", "Location Permission Status"),
                )
            }
        }
        if (onOpenGuide != null) {
            IconButton(onClick = onOpenGuide) {
                Icon(
                    imageVector = Icons.Outlined.HelpOutline,
                    contentDescription = tr(language, "Panduan", "Guide"),
                )
            }
        }
    }
}