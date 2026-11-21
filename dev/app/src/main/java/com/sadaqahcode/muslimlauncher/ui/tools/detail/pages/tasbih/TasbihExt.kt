package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.title
import kotlin.text.ifEmpty

val DefaulDhikrTargets = listOf(33, 99, 100, 0)


fun tr(language: String, id: String, en: String) = if (language == "id") id else en

fun Dhikr.displayTitle(language: String): String = title(language).ifEmpty { arabic }
fun Dhikr.latinText(): String = latin.orEmpty()
fun Dhikr.referenceText(): String = reference.orEmpty()

@Composable
fun Modifier.swipeNavigate(
    enabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
): Modifier {
    val previous by rememberUpdatedState(onPrevious)
    val next by rememberUpdatedState(onNext)
    if (!enabled) return this

    return pointerInput(Unit) {
        var total = 0f
        val threshold = 64.dp.toPx()
        detectHorizontalDragGestures(
            onDragStart = { total = 0f },
            onDragEnd = {
                if (total <= -threshold) next() else if (total >= threshold) previous()
            },
            onDragCancel = { total = 0f },
        ) { _, dragAmount -> total += dragAmount }
    }
}
