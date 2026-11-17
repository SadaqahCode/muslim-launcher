package com.sadaqahcode.muslimlauncher.domain.model

import android.os.UserHandle
import androidx.compose.ui.graphics.ImageBitmap

data class LauncherApp(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap,
    val category: AppCategory = AppCategory.DUNYA_OPTIONAL,
    val isPinned: Boolean = false,
    val isDocked: Boolean = false,
    // ── Work Profile ──
    // 0 = personal, 10 = work (max 1), 11 = private space
    // UserHandle.hashCode() == userId, works on min 24
    val userId: Int = 0,
    val isWork: Boolean = false,
    // Full component, needed to launch correct profile
    // Personal WhatsApp + Work WhatsApp have same packageName, different component + userId
    val componentName: String = "",
    // For LauncherApps.startMainActivity(component, userHandle)
    // Null for personal / old PackageManager path
    val userHandle: UserHandle? = null,
) {
    // Unique key - never use packageName alone for work profile
    // Personal WhatsApp: com.whatsapp|0|...
    // Work WhatsApp:     com.whatsapp|10|...
    val key: String
        get() = "$packageName|$userId|$componentName"

    val isPersonal: Boolean
        get() = !isWork
}