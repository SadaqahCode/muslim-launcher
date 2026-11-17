package com.sadaqahcode.muslimlauncher.ui.home.helper

import android.content.Context
import com.flagodna.debugprint.debugPrint
import com.sadaqahcode.muslimlauncher.service.NotificationShadeService

private const val TAG = "ShadeOpener"

sealed interface ShadeOpenResult {
    /** Reflection path succeeded — silent, no UI needed. */
    data object OpenedSilently : ShadeOpenResult

    /** Accessibility path succeeded. */
    data object OpenedViaAccessibility : ShadeOpenResult

    /** User hasn't enabled the accessibility service — show a prompt. */
    data object NeedsAccessibilityPermission : ShadeOpenResult

    /** User has enabled it, but the system hasn't bound the instance yet. */
    data object EnabledButNotBound : ShadeOpenResult

    /** Enabled and bound, but the global action still failed. */
    data object Failed : ShadeOpenResult
}


internal fun openNotificationShade(context: Context): ShadeOpenResult {
    if (StatusBarHelper.expandNotificationPanel(context)) {
        debugPrint.d("$TAG: opened silently")
        return ShadeOpenResult.OpenedSilently
    }
    debugPrint.d("$TAG: reflection failed, trying accessibility")

    if (NotificationShadeService.instance != null) {
        val ok = NotificationShadeService.expandNotificationShade()
        debugPrint.d("$TAG: accessibility ok=$ok")
        return if (ok) ShadeOpenResult.OpenedViaAccessibility else ShadeOpenResult.Failed
    }

    val enabled = isAccessibilityServiceEnabled(context, NotificationShadeService::class.java)
    debugPrint.d("$TAG: accessibility instance null, enabled=$enabled")
    return if (enabled) ShadeOpenResult.EnabledButNotBound
    else ShadeOpenResult.NeedsAccessibilityPermission
}