
package com.sadaqahcode.muslimlauncher.ui.home.helper

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.abs

@Composable
internal fun Modifier.shadeSwipeGesture(
    threshold: Dp,
    isGatingOk: () -> Boolean,
    onSwipeTriggered: (downPx: Float) -> Unit,
): Modifier {
    val currentGating by rememberUpdatedState(isGatingOk)
    val currentOnSwipe by rememberUpdatedState(onSwipeTriggered)

    val thresholdPx = with(LocalDensity.current) {
        threshold.toPx()
    }

    return pointerInput(thresholdPx) {
        val touchSlop = viewConfiguration.touchSlop

        awaitEachGesture {
            var downAccum = 0f
            var upAccum = 0f
            var horizontalAccum = 0f
            var lifted = false
            var cancelled = false

            val firstDown = awaitPointerEvent(
                PointerEventPass.Initial
            ).changes.firstOrNull() ?: return@awaitEachGesture

            val pointerId = firstDown.id

            while (true) {
                val event = awaitPointerEvent(
                    PointerEventPass.Initial
                )

                // Ignore multi-touch gestures.
                if (event.changes.count { it.pressed } > 1) {
                    cancelled = true
                    break
                }

                val change = event.changes.firstOrNull {
                    it.id == pointerId
                } ?: break

                if (!change.pressed) {
                    lifted = true
                    break
                }

                val delta = change.position - change.previousPosition

                if (delta.y > 0f) {
                    downAccum += delta.y
                } else {
                    upAccum += abs(delta.y)
                }

                horizontalAccum += abs(delta.x)
            }

            if (!lifted || cancelled) {
                return@awaitEachGesture
            }

            val verticalAccum = downAccum + upAccum

            val isDownSwipe =
                downAccum >= thresholdPx &&
                        downAccum > horizontalAccum &&
                        downAccum > upAccum &&
                        verticalAccum > touchSlop

            if (isDownSwipe && currentGating()) {
                currentOnSwipe(downAccum)
            }
        }
    }
}
