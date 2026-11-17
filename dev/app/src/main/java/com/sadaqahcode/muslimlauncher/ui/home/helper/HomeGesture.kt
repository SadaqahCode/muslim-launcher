
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
import com.flagodna.debugprint.debugPrint
import kotlin.math.abs

private const val TAG = "ShadeSwipe"

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

    return this.pointerInput(thresholdPx) {
        debugPrint.d("$TAG: pointerInput coroutine launched")

        val touchSlop = viewConfiguration.touchSlop

        awaitEachGesture {
            var downAccum = 0f
            var upAccum = 0f
            var horizontalAccum = 0f

            var lifted = false
            var eventCount = 0

            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                eventCount++

                val change = event.changes.firstOrNull() ?: break

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

            if (lifted) {
                val gatingOk = currentGating()

                val verticalAccum = downAccum + upAccum
                val verticalDominant = downAccum > horizontalAccum
                val reached = downAccum >= thresholdPx

                val fire = gatingOk &&
                        verticalDominant &&
                        reached &&
                        verticalAccum > touchSlop

                debugPrint.d(
                    "$TAG: end events=$eventCount " +
                            "down=${downAccum.toInt()}px " +
                            "up=${upAccum.toInt()}px " +
                            "horiz=${horizontalAccum.toInt()}px " +
                            "thresh=${thresholdPx.toInt()}px " +
                            "gatingOk=$gatingOk " +
                            "vDom=$verticalDominant " +
                            "reached=$reached fire=$fire"
                )

                if (fire) {
                    currentOnSwipe(downAccum)
                }
            }
        }
    }
}
