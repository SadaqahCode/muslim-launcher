package com.sadaqahcode.muslimlauncher.ui.widgets

import android.appwidget.AppWidgetHostView
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.RemoteViews
import android.widget.TextView
import com.flagodna.debugprint.debugPrint
import kotlin.math.abs

private const val TAG = "WidgetHostView"

internal class LauncherAppWidgetHostView @JvmOverloads constructor(
    context: Context,
    var errorTextColor: Int = Color.GRAY
) : AppWidgetHostView(context) {

    private var hasPerformedLongPress: Boolean = false
    private var pendingCheckForLongPress: CheckForLongPress? = null

    private var downX: Float = 0f
    private var downY: Float = 0f
    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    private var lastReportedWidthDp: Int = -1
    private var lastReportedHeightDp: Int = -1

    private val sizeOptions = Bundle()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        debugPrint.d(TAG, "[WidgetFlow] [$appWidgetId] attached")
    }

    override fun onDetachedFromWindow() {
        debugPrint.d(TAG, "[WidgetFlow] [$appWidgetId] detached")
        cancelPendingLongPress()
        super.onDetachedFromWindow()
    }

    override fun updateAppWidget(remoteViews: RemoteViews?) {
        val id = appWidgetId
        if (remoteViews == null) {
            debugPrint.w(
                TAG,
                "[WidgetFlow] [$id] updateAppWidget remoteViews=null " +
                        "(provider sent nothing — will call getDefaultView)"
            )
        } else {
            debugPrint.d(TAG, "[WidgetFlow] [$id] updateAppWidget remoteViews=non-null")
        }
        super.updateAppWidget(remoteViews)
    }

    override fun getDefaultView(): View {
        debugPrint.w(TAG, "[WidgetFlow] [$appWidgetId] getDefaultView — null RemoteViews")
        return errorView("Widget sent no layout")
    }

    override fun getErrorView(): View {
        debugPrint.e(TAG, "[WidgetFlow] [$appWidgetId] getErrorView — inflation failed")
        return errorView("Widget could not be loaded")
    }

    private fun errorView(message: String): View =
        TextView(context).apply {
            text = message
            gravity = Gravity.CENTER
            setTextColor(errorTextColor)
            setPadding(16, 16, 16, 16)
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)
        val density = resources.displayMetrics.density
        val wDp = (wSize / density).toInt()
        val hDp = (hSize / density).toInt()

        if (wDp > 0 && hDp > 0 &&
            (wDp != lastReportedWidthDp || hDp != lastReportedHeightDp)
        ) {
            lastReportedWidthDp = wDp
            lastReportedHeightDp = hDp

            sizeOptions.clear()
            @Suppress("DEPRECATION")
            updateAppWidgetSize(sizeOptions, wDp, hDp, wDp, hDp)

            debugPrint.d(TAG, "[WidgetFlow] [$appWidgetId] size reported ${wDp}x${hDp}dp")
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        debugPrint.d(
            TAG,
            "[WidgetFlow] [$appWidgetId] measure " +
                    "wSpec=${MeasureSpec.toString(widthMeasureSpec)} " +
                    "hSpec=${MeasureSpec.toString(heightMeasureSpec)} " +
                    "→ measuredW=$measuredWidth measuredH=$measuredHeight"
        )
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (hasPerformedLongPress) {
            hasPerformedLongPress = false
            return true
        }
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
                postCheckForLongClick()
            }
            MotionEvent.ACTION_MOVE -> {
                if (!hasPerformedLongPress) {
                    if (abs(ev.x - downX) > touchSlop || abs(ev.y - downY) > touchSlop) {
                        cancelPendingLongPress()
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                hasPerformedLongPress = false
                cancelPendingLongPress()
            }
        }
        return false
    }

    private fun postCheckForLongClick() {
        hasPerformedLongPress = false
        if (pendingCheckForLongPress == null) pendingCheckForLongPress = CheckForLongPress()
        pendingCheckForLongPress?.rememberWindowAttachCount()
        postDelayed(pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout().toLong())
    }

    override fun cancelLongPress() {
        super.cancelLongPress()
        hasPerformedLongPress = false
        cancelPendingLongPress()
    }

    private fun cancelPendingLongPress() {
        pendingCheckForLongPress?.let { removeCallbacks(it) }
    }

    private inner class CheckForLongPress : Runnable {
        private var originalWindowAttachCount: Int = 0
        override fun run() {
            if (parent != null && hasWindowFocus() &&
                originalWindowAttachCount == windowAttachCount && !hasPerformedLongPress
            ) {
                if (performLongClick()) hasPerformedLongPress = true
            }
        }
        fun rememberWindowAttachCount() {
            originalWindowAttachCount = windowAttachCount
        }
    }
}