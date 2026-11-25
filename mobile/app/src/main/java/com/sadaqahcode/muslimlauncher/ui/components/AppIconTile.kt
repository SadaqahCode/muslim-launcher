package com.sadaqahcode.muslimlauncher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.data.local.launcher.shapeFor
import com.sadaqahcode.muslimlauncher.domain.model.AppItemLayout
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors

@Composable
fun PinnedAppItem(
    app: LauncherApp,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    settings: UserSettings,
    modifier: Modifier = Modifier,
) {
    val lc = LocalLegibleColors.current
    val iconSize = settings.iconSize.dp
    val textSize = settings.textSize.sp

    when (settings.itemLayout) {
        AppItemLayout.TextBelow -> Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Image(
                bitmap = app.icon,
                contentDescription = app.label,
                modifier = Modifier.size(iconSize),
            )
            Text(
                text = app.label,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = textSize),
                color = lc.scheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        AppItemLayout.TextBeside -> Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                bitmap = app.icon,
                contentDescription = app.label,
                modifier = Modifier.size(iconSize),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = app.label,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = textSize),
                color = lc.scheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        AppItemLayout.IconOnly -> Image(
            bitmap = app.icon,
            contentDescription = app.label,
            modifier = modifier
                .size(iconSize)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        )
    }
}

@Composable
fun DockIcon(
    app: LauncherApp?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    settings: UserSettings,
    modifier: Modifier = Modifier,
) {
    val lc = LocalLegibleColors.current
    val iconSize = settings.iconSize.dp
    val shape = shapeFor(settings.iconShape)

    if (app != null) {
        Image(
            bitmap = app.icon,
            contentDescription = app.label,
            modifier = modifier
                .size(iconSize)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        )
    } else {
        Box(
            modifier = modifier
                .size(iconSize)
                .border(
                    width = 1.dp,
                    color = lc.scheme.onBackground.copy(alpha = 0.25f),
                    shape = shape,
                ),
        )
    }
}