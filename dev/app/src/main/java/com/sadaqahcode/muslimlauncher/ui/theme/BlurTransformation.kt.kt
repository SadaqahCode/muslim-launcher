package com.sadaqahcode.muslimlauncher.ui.theme

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.os.Build
import coil3.size.Size
import coil3.transform.Transformation

class BlurTransformation(
    private val radius: Float,
) : Transformation() {
    override val cacheKey: String = "blur:$radius"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        if (radius <= 0f) return input
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return input

        // Hardware bitmap can't be drawn on Canvas - copy to software first
        val safeInput = if (input.config == Bitmap.Config.HARDWARE) {
            input.copy(Bitmap.Config.ARGB_8888, false) ?: return input
        } else input

        val smallW = (safeInput.width * 0.5f).toInt().coerceAtLeast(1)
        val smallH = (safeInput.height * 0.5f).toInt().coerceAtLeast(1)
        val small = Bitmap.createScaledBitmap(safeInput, smallW, smallH, true)

        val clamped = radius.coerceIn(0f, 25f)
        val blurredSmall = Bitmap.createBitmap(smallW, smallH, Bitmap.Config.ARGB_8888)
        val renderNode = RenderNode("blur")
        renderNode.setPosition(0, 0, smallW, smallH)
        renderNode.setRenderEffect(
            RenderEffect.createBlurEffect(clamped, clamped, Shader.TileMode.CLAMP)
        )
        val nodeCanvas = renderNode.beginRecording()
        nodeCanvas.drawBitmap(small, 0f, 0f, null)
        renderNode.endRecording()
        Canvas(blurredSmall).drawRenderNode(renderNode)

        return Bitmap.createScaledBitmap(blurredSmall, safeInput.width, safeInput.height, true)
    }
}