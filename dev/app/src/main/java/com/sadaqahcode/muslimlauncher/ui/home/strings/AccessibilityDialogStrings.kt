package com.sadaqahcode.muslimlauncher.ui.home.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource
import com.sadaqahcode.muslimlauncher.R

@Immutable
data class AccessibilityDialogStrings(
    val titleLock: String,
    val titleShade: String,
    val subtitleLock: String,
    val subtitleShade: String,
    val infoShade: String,
    val continueLabel: String,
    val openSettingsLabel: String,
    val notNowLabel: String,
    val accessibilityTitle: String,
    val accessibilityDesc: String,
    val accessibilityBadge: String,
    val deviceAdminTitle: String,
    val deviceAdminDesc: String,
    val deviceAdminBadge: String,
    val enabledLabel: String,
)

@Composable
fun rememberAccessibilityDialogStrings(): AccessibilityDialogStrings =
    AccessibilityDialogStrings(
        titleLock = stringResource(R.string.accessibility_dialog_title_lock),
        titleShade = stringResource(R.string.accessibility_dialog_title_shade),
        subtitleLock = stringResource(R.string.accessibility_dialog_subtitle_lock),
        subtitleShade = stringResource(R.string.accessibility_dialog_subtitle_shade),

        infoShade = stringResource(R.string.accessibility_dialog_info_shade),

        continueLabel = stringResource(R.string.accessibility_dialog_continue),
        openSettingsLabel = stringResource(R.string.accessibility_dialog_open_settings),
        notNowLabel = stringResource(R.string.accessibility_dialog_not_now),

        accessibilityTitle = stringResource(R.string.accessibility_dialog_lock_accessibility_title),
        accessibilityDesc = stringResource(R.string.accessibility_dialog_lock_accessibility_desc),
        accessibilityBadge = stringResource(R.string.accessibility_dialog_lock_accessibility_badge),

        deviceAdminTitle = stringResource(R.string.accessibility_dialog_lock_device_admin_title),
        deviceAdminDesc = stringResource(R.string.accessibility_dialog_lock_device_admin_desc),
        deviceAdminBadge = stringResource(R.string.accessibility_dialog_lock_device_admin_badge),

        enabledLabel = stringResource(R.string.accessibility_dialog_enabled),
    )