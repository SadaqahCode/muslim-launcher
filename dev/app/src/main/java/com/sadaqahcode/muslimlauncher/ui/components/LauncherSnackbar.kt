package com.sadaqahcode.muslimlauncher.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

enum class LauncherSnackbarType {
    Info,
    Success,
    Warning,
    Error,
}

class LauncherSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val type: LauncherSnackbarType = LauncherSnackbarType.Info,
    override val withDismissAction: Boolean = true,
) : SnackbarVisuals

suspend fun SnackbarHostState.showLauncherSnackbar(
    message: String,
    type: LauncherSnackbarType = LauncherSnackbarType.Info,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult {
    val result = showSnackbar(
        LauncherSnackbarVisuals(
            message = message,
            actionLabel = actionLabel,
            duration = duration,
            type = type,
        )
    )
    if (result == SnackbarResult.ActionPerformed) {
        onAction?.invoke()
    }
    return result
}

@Composable
fun LauncherSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
    ) { data ->
        LauncherSnackbar(data = data)
    }
}

@Composable
fun LauncherSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
) {
    val visuals = data.visuals as? LauncherSnackbarVisuals
    val type = visuals?.type ?: LauncherSnackbarType.Info

    // Material color, directly
    val containerColor = when (type) {
        LauncherSnackbarType.Info -> MaterialTheme.colorScheme.inverseSurface
        LauncherSnackbarType.Success -> MaterialTheme.colorScheme.primaryContainer
        LauncherSnackbarType.Warning -> MaterialTheme.colorScheme.tertiaryContainer
        LauncherSnackbarType.Error -> MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = when (type) {
        LauncherSnackbarType.Info -> MaterialTheme.colorScheme.inverseOnSurface
        LauncherSnackbarType.Success -> MaterialTheme.colorScheme.onPrimaryContainer
        LauncherSnackbarType.Warning -> MaterialTheme.colorScheme.onTertiaryContainer
        LauncherSnackbarType.Error -> MaterialTheme.colorScheme.onErrorContainer
    }

    // Simple swipe to close: drag left / right, release past threshold = dismiss
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .alpha((1f - abs(offsetX.value) / 600f).coerceIn(0f, 1f))
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    scope.launch { offsetX.snapTo(offsetX.value + delta) }
                },
                onDragStopped = {
                    scope.launch {
                        if (abs(offsetX.value) > 150f) {
                            offsetX.animateTo(
                                if (offsetX.value > 0) 1000f else -1000f,
                                tween(150)
                            )
                            data.dismiss()
                        } else {
                            offsetX.animateTo(0f, spring())
                        }
                    }
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = 6.dp,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 4.dp,
                top = 12.dp,
                bottom = 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = data.visuals.message,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                modifier = Modifier.weight(1f, fill = false),
            )

            // Optional action, only shows if actionLabel is set
            if (data.visuals.actionLabel != null) {
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { data.performAction() }) {
                    Text(
                        text = data.visuals.actionLabel!!,
                        color = contentColor
                    )
                }
            }

            IconButton(onClick = { data.dismiss() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = contentColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}