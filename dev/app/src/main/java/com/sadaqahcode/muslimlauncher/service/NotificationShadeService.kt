package com.sadaqahcode.muslimlauncher.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityEvent

@SuppressLint("AccessibilityPolicy")
class NotificationShadeService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) { /* not needed */ }

    override fun onInterrupt() { /* not needed */ }

    companion object {
        /** Set by the service when it's connected; null otherwise. */
        var instance: NotificationShadeService? = null
            private set

        fun expandNotificationShade(): Boolean {
            val svc = instance ?: return false
            return svc.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
        }
    }

    override fun onServiceConnected() {
        instance = this
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }
}