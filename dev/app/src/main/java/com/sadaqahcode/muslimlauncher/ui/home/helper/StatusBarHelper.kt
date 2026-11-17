package com.sadaqahcode.muslimlauncher.ui.home.helper

import android.content.Context
import android.os.Build
import com.flagodna.debugprint.debugPrint

private const val TAG = "StatusBarHelper"

internal object StatusBarHelper {

    fun expandNotificationPanel(context: Context): Boolean = try {
        val statusBarManager = context.getSystemService("statusbar")
        val clazz = Class.forName("android.app.StatusBarManager")
        val method = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            clazz.getMethod("expandNotificationsPanel")
        } else {
            clazz.getMethod("expand")
        }
        method.invoke(statusBarManager)
        debugPrint.d("$TAG: reflection ok")
        true
    } catch (e: Exception) {
        debugPrint.e("$TAG: reflection failed: ${e.message}")
        false
    }
}