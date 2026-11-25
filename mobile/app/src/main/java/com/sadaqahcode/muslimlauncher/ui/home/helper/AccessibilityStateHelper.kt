package com.sadaqahcode.muslimlauncher.ui.home.helper

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import com.flagodna.debugprint.debugPrint

internal fun isAccessibilityServiceEnabled(
    context: Context,
    serviceClass: Class<*>,
): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices =
        am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

    debugPrint.d("AccessCheck", "enabled=${enabledServices.map { it.id }} want=${serviceClass.name}")

    return enabledServices.any {
        it.resolveInfo.serviceInfo.packageName == context.packageName &&
                it.resolveInfo.serviceInfo.name == serviceClass.name
    }
}