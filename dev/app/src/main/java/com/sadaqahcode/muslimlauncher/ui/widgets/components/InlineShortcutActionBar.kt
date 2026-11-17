package com.sadaqahcode.muslimlauncher.ui.widgets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InlineShortcutActionBar(
    onRemove: () -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: nothing — shortcuts have no preset/size to pick. The
        // Spacer(weight) pushes the actions to the right edge, matching
        // InlineActionBar's layout where the preset lives on the left.
        Spacer(Modifier.weight(1f))

        // Right: [Delete] [Close] — same order as InlineActionBar.
        Surface(
            onClick = onRemove,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove shortcut",
                modifier = Modifier.padding(8.dp).size(20.dp),
            )
        }

        Surface(
            onClick = onClose,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Done",
                modifier = Modifier.padding(8.dp).size(20.dp),
            )
        }
    }
}