package com.sadaqahcode.muslimlauncher.ui.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import com.flagodna.debugprint.debugPrint

private const val TAG = "WidgetHost"

internal class LauncherAppWidgetHost(
    context: Context,
    hostId: Int,
) : AppWidgetHost(context, hostId) {

    override fun onCreateView(
        context: Context,
        appWidgetId: Int,
        appWidget: AppWidgetProviderInfo,
    ): AppWidgetHostView {
        debugPrint.d(
            TAG,
            "[WidgetFlow] onCreateView id=$appWidgetId " +
                    "provider=${appWidget.provider.flattenToString()} → LauncherAppWidgetHostView"
        )
        return LauncherAppWidgetHostView(context)
    }
}