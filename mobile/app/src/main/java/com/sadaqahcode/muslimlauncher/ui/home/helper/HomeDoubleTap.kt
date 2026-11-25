package com.sadaqahcode.muslimlauncher.ui.home.helper

import android.os.SystemClock
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.flagodna.debugprint.debugPrint

@Composable
internal fun Modifier.doubleTapGesture(
    onDoubleTap: () -> Unit,
    isGatingOk: () -> Boolean = { true },
): Modifier {
    val currentOnDoubleTap by rememberUpdatedState(onDoubleTap)
    val currentGating by rememberUpdatedState(isGatingOk)
    val density = LocalDensity.current
    val doubleTapSlop = with(density) { 100.dp.toPx() }
    val doubleTapTimeout = 500L

    return pointerInput(Unit) {
        var lastTapTime = 0L
        var lastTapPos = Offset.Zero
        val touchSlop = viewConfiguration.touchSlop
        val longPressTimeout = viewConfiguration.longPressTimeoutMillis

        awaitEachGesture {
            val firstDown = awaitFirstDown(
                requireUnconsumed = true,
                pass = PointerEventPass.Main
            )

            val up = waitForUpOrCancellation(
                pass = PointerEventPass.Main
            ) ?: return@awaitEachGesture

            // Fixed: use isConsumed, not consumed.downChange
            if (up.isConsumed) return@awaitEachGesture

            // Long press is NOT a tap
            val pressDuration = up.uptimeMillis - firstDown.uptimeMillis
            if (pressDuration > longPressTimeout) {
                lastTapTime = 0L
                lastTapPos = Offset.Zero
                return@awaitEachGesture
            }

            if (!currentGating()) {
                lastTapTime = 0L
                lastTapPos = Offset.Zero
                return@awaitEachGesture
            }

            val moveDist = (up.position - firstDown.position).getDistance()
            if (moveDist > touchSlop) {
                lastTapTime = 0L
                return@awaitEachGesture
            }

            val now = SystemClock.uptimeMillis()
            val deltaTime = now - lastTapTime
            val dist = (firstDown.position - lastTapPos).getDistance()
            val isDouble = lastTapTime != 0L &&
                    deltaTime < doubleTapTimeout &&
                    dist < doubleTapSlop

            if (isDouble) {
                debugPrint.d("DoubleTap", "2x TAP DETECTED -> call onDoubleTapHome()")
                lastTapTime = 0L
                lastTapPos = Offset.Zero
                if (currentGating()) currentOnDoubleTap()
            } else {
                debugPrint.d("DoubleTap", "1x TAP RECORDED")
                lastTapTime = now
                lastTapPos = firstDown.position
            }
        }
    }
}