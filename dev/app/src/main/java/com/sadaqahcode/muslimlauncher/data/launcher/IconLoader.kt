package com.sadaqahcode.muslimlauncher.data.launcher

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape as ComposeRectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.flagodna.debugprint.debugPrint
import com.sadaqahcode.muslimlauncher.domain.model.AppIconShape
import androidx.core.graphics.createBitmap

internal fun shapeFor(iconShape: AppIconShape): Shape = when (iconShape) {
    AppIconShape.Default -> ComposeRectangleShape
    AppIconShape.Box -> RoundedCornerShape(percent = 15)
    AppIconShape.Rounded -> RoundedCornerShape(percent = 30)
    AppIconShape.Circle -> CircleShape
}

@SuppressLint("NewApi")
internal fun Drawable.toShapedBitmap(
    sizePx: Int,
    shape: Shape,
    density: Density,
): ImageBitmap {
    val bitmap = createBitmap(sizePx, sizePx)
    val canvas = Canvas(bitmap)

    val clipPath = Path().apply {
        addOutline(
            shape.createOutline(
                Size(sizePx.toFloat(), sizePx.toFloat()),
                LayoutDirection.Ltr,
                density,
            )
        )
    }
    canvas.clipPath(clipPath.asAndroidPath())

    val whitePaint = Paint().apply { color = Color.WHITE }
    canvas.drawRect(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), whitePaint)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this is AdaptiveIconDrawable) {
        debugPrint.d(
            "adaptive — bg=${background?.javaClass?.simpleName}, " +
                    "fg=${foreground?.javaClass?.simpleName}"
        )
        drawAdaptiveUnmasked(canvas, sizePx)
    } else {
        debugPrint.d("legacy — ${this::class.simpleName}")
        setBounds(0, 0, sizePx, sizePx)
        draw(canvas)
    }

    return bitmap.asImageBitmap()
}

@RequiresApi(Build.VERSION_CODES.O)
private fun AdaptiveIconDrawable.drawAdaptiveUnmasked(canvas: Canvas, sizePx: Int) {
    val inset = (sizePx * AdaptiveIconDrawable.getExtraInsetFraction()).toInt()
    val layerBounds = Rect(-inset, -inset, sizePx + inset, sizePx + inset)

    background?.let {
        it.bounds = layerBounds
        it.draw(canvas)
    }
    foreground?.let {
        it.bounds = layerBounds
        it.draw(canvas)
    }
}