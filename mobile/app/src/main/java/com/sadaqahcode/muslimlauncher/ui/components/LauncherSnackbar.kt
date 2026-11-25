package com.sadaqahcode.muslimlauncher.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import kotlinx.coroutines.launch
import kotlin.math.abs


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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            LauncherSnackbar(data = data, modifier = Modifier.widthIn(max = 560.dp))
        }
    }
}
@Composable
fun LauncherSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val visuals = data.visuals
    val type = (visuals as? LauncherSnackbarVisuals)?.type ?: LauncherSnackbarType.Info

    val tint: Color = when (type) {
        LauncherSnackbarType.Info -> colors.inverseOnSurface
        LauncherSnackbarType.Success -> colors.inversePrimary
        LauncherSnackbarType.Warning -> colors.tertiary
        LauncherSnackbarType.Error -> colors.error
    }
    val icon = when (type) {
        LauncherSnackbarType.Info -> Icons.Default.Info
        LauncherSnackbarType.Success -> Icons.Default.Check
        LauncherSnackbarType.Warning -> Icons.Default.Warning
        LauncherSnackbarType.Error -> Icons.Default.Close
    }

    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var widthPx by remember { mutableIntStateOf(0) }
    val dismissDistance = with(LocalDensity.current) { 96.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { widthPx = it.width }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    scope.launch { offsetX.snapTo(offsetX.value + delta) }
                },
                onDragStopped = { velocity ->
                    scope.launch {
                        val far = abs(offsetX.value) > dismissDistance
                        val fast = abs(velocity) > 1200f
                        if (far || fast) {
                            val direction = when {
                                offsetX.value != 0f -> if (offsetX.value > 0f) 1f else -1f
                                else -> if (velocity > 0f) 1f else -1f
                            }
                            offsetX.animateTo(direction * widthPx.coerceAtLeast(1), tween(160))
                            data.dismiss()
                        } else {
                            offsetX.animateTo(0f, spring(dampingRatio = 0.8f))
                        }
                    }
                },
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationX = offsetX.value
                    alpha = (1f - abs(offsetX.value) / widthPx.coerceAtLeast(1)).coerceIn(0f, 1f)
                }
                .clip(RoundedCornerShape(22.dp))
                .background(colors.inverseSurface)
                .clickable { data.dismiss() }
                .heightIn(min = 56.dp)
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
                .semantics {
                    liveRegion = LiveRegionMode.Polite
                    customActions = listOf(
                        CustomAccessibilityAction("Dismiss") {
                            data.dismiss()
                            true
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(18.dp),
                )
            }

            Text(
                text = visuals.message,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.inverseOnSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            )

            val actionLabel = visuals.actionLabel
            if (actionLabel != null) {
                Box(
                    modifier = Modifier
                        .heightIn(min = 36.dp)
                        .clip(CircleShape)
                        .background(colors.inverseOnSurface.copy(alpha = 0.10f))
                        .clickable { data.performAction() }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.inverseOnSurface,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}