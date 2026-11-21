package com.sadaqahcode.muslimlauncher.domain.model.tools

/**
 * User's preference for tactile feedback on interactive elements.
 *
 * [Haptic]     Semantic system haptics (`HapticFeedbackType`). Respects the user's
 *              "Touch interactions" toggle in system settings. Best default —
 *              OEM-tuned, battery-friendly.
 * [Vibration]  Raw `Vibrator` API. Ignores the "Touch interactions" toggle, so it
 *              fires even when the user disabled system haptics. Requires
 *              `<uses-permission android:name="android.permission.VIBRATE" />`
 *              (normal, auto-granted). Still respects ringer-mode / DND.
 * [Silent]     No tactile feedback.
 */
enum class HapticMode {
    Haptic,
    Vibration,
    Silent,
}