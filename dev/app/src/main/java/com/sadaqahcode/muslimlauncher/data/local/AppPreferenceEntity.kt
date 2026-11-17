package com.sadaqahcode.muslimlauncher.data.local

import androidx.room.Entity

@Entity(
    tableName = "app_preferences",
    primaryKeys = ["packageName", "userId"]
)
data class AppPreferenceEntity(
    val packageName: String,
    val userId: Int = 0, // 0 = personal, 10 = work
    val category: String = "DUNYA_OPTIONAL",
    val isPinned: Boolean = false,
    val isDocked: Boolean = false,
)