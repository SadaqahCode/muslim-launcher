package com.sadaqahcode.muslimlauncher.ui.widgets.components

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.sadaqahcode.muslimlauncher.data.local.ShortcutEntity


@Composable
internal fun ShortcutTile(
    shortcut: ShortcutEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isEditing: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val pm = context.packageManager

    val icon = remember(shortcut.packageName, shortcut.activityName) {
        runCatching {
            val intent = Intent().apply {
                setClassName(shortcut.packageName, shortcut.activityName)
            }
            val info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                ?: pm.getPackageInfo(shortcut.packageName, 0).applicationInfo?.let { null }
            info?.loadIcon(pm)?.toBitmap(96, 96)?.asImageBitmap()
        }.getOrNull()
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isEditing) onLongClick() else onClick()
                },
                onLongClick = onLongClick,
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.80f),
        border = if (isEditing) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (icon != null) {
                Image(
                    bitmap = icon,
                    contentDescription = shortcut.label,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            }
            Text(
                text = shortcut.label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}