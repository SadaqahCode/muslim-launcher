package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun BottomBar(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    animate: Boolean = true,
) {
    val lc = LocalLegibleColors.current
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val openDrawer by rememberUpdatedState(onOpenDrawer)

    val thresholdPx = with(density) { 24.dp.toPx() }
    val maxDragPx = with(density) { 40.dp.toPx() }
    val flickVelocity = 700f

    var loaded by remember { mutableStateOf(!animate) }
    LaunchedEffect(animate) {
        if (!animate) {
            loaded = true
            return@LaunchedEffect
        }
        kotlinx.coroutines.delay(150.milliseconds)
        loaded = true
    }
    val enterOffset by animateFloatAsState(
        targetValue = if (!animate || loaded) 0f else 30f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "enterOffset",
    )
    val enterAlpha by animateFloatAsState(
        targetValue = if (!animate || loaded) 1f else 0f,
        animationSpec = tween(400),
        label = "enterAlpha",
    )

    val bobAnim = remember { Animatable(0f) }
    LaunchedEffect(animate) {
        if (!animate) {
            bobAnim.snapTo(0f)
            return@LaunchedEffect
        }
        repeat(3) {
            bobAnim.animateTo(-6f, tween(700, easing = FastOutSlowInEasing))
            bobAnim.animateTo(0f, tween(700, easing = FastOutSlowInEasing))
        }
    }

    // ── Drag ──
    var dragOffset by remember { mutableFloatStateOf(0f) } // 0 .. -maxDragPx
    var isDragging by remember { mutableStateOf(false) }

    val shown by animateFloatAsState(
        targetValue = dragOffset,
        animationSpec = if (isDragging) snap() else spring(
            dampingRatio = 0.7f,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "dragShown",
    )

    val ready = -dragOffset >= thresholdPx
    LaunchedEffect(ready) {
        if (ready) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    val flow = remember { Animatable(0f) }
    LaunchedEffect(isDragging) {
        if (isDragging) {
            while (true) {
                flow.snapTo(0f)
                flow.animateTo(1f, tween(800, easing = LinearEasing))
            }
        } else {
            flow.snapTo(0f)
        }
    }

    val dragState = rememberDraggableState { delta ->
        dragOffset = (dragOffset + delta).coerceIn(-maxDragPx, 0f)
    }

    val ink = lc.scheme.onBackground
    val progress = (-shown / thresholdPx).coerceIn(0f, 1f)

    Column(
        modifier = modifier.graphicsLayer {
            alpha = enterAlpha
            translationY = enterOffset
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .drawBehind {
                    val len = -shown
                    if (len < 2f) return@drawBehind

                    val cx = size.width / 2f
                    val originY = size.height / 2f
                    val gap = 14.dp.toPx()

                    val radius = (if (ready) 2.5f else 2f).dp.toPx()
                    val baseAlpha = if (ready) 0.9f else 0.7f
                    var d = flow.value * gap
                    while (d <= len) {
                        val t = d / len
                        val appear = (d / gap).coerceAtMost(1f)
                        val alpha = appear * (1f - 0.6f * t) * baseAlpha
                        drawCircle(
                            color = ink.copy(alpha = alpha),
                            radius = radius,
                            center = Offset(cx, originY - d),
                        )
                        d += gap
                    }
                }
                .clickable(onClickLabel = "Open all apps", onClick = { openDrawer() })
                .draggable(
                    state = dragState,
                    orientation = Orientation.Vertical,
                    onDragStarted = { isDragging = true },
                    onDragStopped = { velocity ->
                        val open = -dragOffset >= thresholdPx || velocity < -flickVelocity
                        isDragging = false
                        dragOffset = 0f
                        if (open) openDrawer()
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.graphicsLayer {
                    translationY = shown + bobAnim.value
                },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width((40 + 24 * progress).dp)
                        .height((8 - 1 * progress).dp)
                        .background(
                            ink.copy(alpha = if (ready) 0.9f else 0.5f + 0.25f * progress),
                            CircleShape,
                        )
                )
            }
        }
    }
}