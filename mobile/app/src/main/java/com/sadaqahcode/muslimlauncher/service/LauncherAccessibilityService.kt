package com.sadaqahcode.muslimlauncher.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import com.flagodna.debugprint.debugPrint

private const val TAG = "LockService"

@SuppressLint("AccessibilityPolicy")
class LauncherAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) { /* not needed */ }

    override fun onInterrupt() { /* not needed */ }

    companion object {
        var instance: LauncherAccessibilityService? = null
            private set

        @SuppressLint("NewApi")
        fun lockScreen(): Boolean {
            debugPrint.d(TAG, "lockScreen() called, sdk=${Build.VERSION.SDK_INT}, instance=${instance != null}")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                debugPrint.d(TAG, "API < 28, return false")
                return false
            }
            val svc = instance ?: run {
                debugPrint.e(TAG, "instance null, service not bound / not enabled")
                return false
            }
            val result = svc.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            debugPrint.d(TAG, "performGlobalAction LOCK_SCREEN result=$result")
            return result
        }
    }

    override fun onServiceConnected() {
        debugPrint.d(TAG, "onServiceConnected")
        instance = this
    }

    override fun onDestroy() {
        debugPrint.d(TAG, "onDestroy")
        instance = null
        super.onDestroy()
    }
}