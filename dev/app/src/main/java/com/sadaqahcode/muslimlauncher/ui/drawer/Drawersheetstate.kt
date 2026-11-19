package com.sadaqahcode.muslimlauncher.ui.drawer

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

private val SettleSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = 700f,
    visibilityThreshold = 0.001f,
)

/** Close when dragged down past 25% of screen height (progress < 0.75). */
private const val CLOSE_POSITION_THRESHOLD = 0.75f

private const val UNINITIALIZED_HEIGHT = Float.NaN


private const val MAX_INITIAL_VELOCITY = 8f

/** Tolerance for "already at target" checks — animate() can land on 0.9999. */
private const val SETTLED_EPSILON = 1e-4f

@Stable
class DrawerSheetState internal constructor(
    initiallyOpen: Boolean,
    private val scope: CoroutineScope,
    private val velocityThresholdPx: Float,
    private val onOpenRequested: () -> Unit,
    private val onCloseRequested: () -> Unit,
) {
    var progress by mutableFloatStateOf(if (initiallyOpen) 1f else 0f)
        private set

    var heightPx: Float = UNINITIALIZED_HEIGHT

    internal var targetOpen: Boolean = initiallyOpen
        private set

    private var settleJob: Job? = null

    private val isMeasured: Boolean
        get() = heightPx.isFinite() && heightPx > 0f

    fun dragBy(upwardPx: Float): Float {
        if (!isMeasured) return 0f
        settleJob?.cancel()
        val target = (progress + upwardPx / heightPx).coerceIn(0f, 1f)
        val used = (target - progress) * heightPx
        progress = target
        return used
    }

    fun settle(velocityY: Float) {
        if (!isMeasured) {
            if (targetOpen) onOpenRequested() else onCloseRequested()
            return
        }
        val upwardPerSec = -velocityY
        val open = when {
            upwardPerSec > velocityThresholdPx -> true
            upwardPerSec < -velocityThresholdPx -> false
            else -> progress > CLOSE_POSITION_THRESHOLD
        }
        if (open) onOpenRequested() else onCloseRequested()

        val velocityProgress = (upwardPerSec / heightPx)
            .coerceIn(-MAX_INITIAL_VELOCITY, MAX_INITIAL_VELOCITY)
        animateTo(open, velocity = velocityProgress)
    }

    internal fun animateTo(open: Boolean, velocity: Float = 0f) {
        val target = if (open) 1f else 0f
        if (velocity == 0f && abs(progress - target) < SETTLED_EPSILON) {
            targetOpen = open
            progress = target  // snap in case animate() landed on 0.9999 last time
            return
        }
        targetOpen = open
        settleJob?.cancel()
        settleJob = scope.launch {
            animate(
                initialValue = progress,
                targetValue = target,
                initialVelocity = velocity,
                animationSpec = SettleSpec,
            ) { value, _ -> progress = value }
            // Spring stops *near* the target (visibilityThreshold), not exactly
            // on it — snap so downstream frame math is exact.
            progress = target
        }
    }

    fun snapTo(open: Boolean) {
        settleJob?.cancel()
        targetOpen = open
        progress = if (open) 1f else 0f
    }
}

@Composable
fun rememberDrawerSheetState(
    open: Boolean,
    onOpenRequested: () -> Unit,
    onCloseRequested: () -> Unit,
): DrawerSheetState {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val currentOpen by rememberUpdatedState(onOpenRequested)
    val currentClose by rememberUpdatedState(onCloseRequested)

    val state = remember {
        DrawerSheetState(
            initiallyOpen = open,
            scope = scope,
            // 200 dp/s is enough to trigger open/close on a flick.
            velocityThresholdPx = with(density) { 200.dp.toPx() },
            onOpenRequested = { currentOpen() },
            onCloseRequested = { currentClose() },
        )
    }

    LaunchedEffect(open) {
        if (state.targetOpen != open) state.animateTo(open)
    }
    return state
}