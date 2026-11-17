package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun BottomBar(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    animate: Boolean = true, // true = run animate, false = stop animate, static
) {
    val lc = LocalLegibleColors.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var loaded by remember { mutableStateOf(!animate) }

    LaunchedEffect(animate) {
        if (!animate) {
            loaded = true
            return@LaunchedEffect
        }
        // small delay so it pops after home loads
        kotlinx.coroutines.delay(150.milliseconds)
        loaded = true
    }

    // if animate = false -> 0f / 1f instantly, no entrance
    val animatedEnterOffset by animateFloatAsState(
        targetValue = if (!animate || loaded) 0f else 30f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "enterOffset"
    )
    val animatedAlpha by animateFloatAsState(
        targetValue = if (!animate || loaded) 1f else 0f,
        animationSpec = tween(400),
        label = "enterAlpha"
    )

    // Bobbing up / down - hint to swipe
    val infinite = rememberInfiniteTransition(label = "bob")
    val bobAnimated by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -6f, // up 6px
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    // stop animate -> 0f, static
    val bob = if (animate) bobAnimated else 0f

    // Swipeable top
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = with(density) { 60.dp.toPx() }

    val dragState = rememberDraggableState { delta ->
        // only allow drag up (negative), ignore drag down
        if (delta < 0f || dragOffset + delta < 0f) {
            dragOffset = (dragOffset + delta).coerceIn(-120f, 0f)
        }
    }

    Column(
        modifier = modifier
            .alpha(animatedAlpha)
            .offset { IntOffset(0, (animatedEnterOffset + bob + dragOffset).roundToInt()) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .draggable(
                    state = dragState,
                    orientation = Orientation.Vertical,
                    onDragStopped = { velocity ->
                        // swipe up fast OR far enough -> open drawer
                        if (dragOffset < -swipeThreshold || velocity < -600f) {
                            onOpenDrawer()
                        }
                        // snap back
                        scope.launch {
                            dragOffset = 0f
                        }
                    }
                )
                .clickable(onClickLabel = "Open all apps", onClick = onOpenDrawer),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        lc.scheme.onBackground.copy(alpha = 0.5f),
                        CircleShape,
                    )
            )
        }
    }
}