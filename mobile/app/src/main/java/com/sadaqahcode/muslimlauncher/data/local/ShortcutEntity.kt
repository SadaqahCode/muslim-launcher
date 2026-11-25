package com.sadaqahcode.muslimlauncher.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shortcut_placements")
data class ShortcutEntity(
    @PrimaryKey val id: String,
    val packageName: String,
    val activityName: String,
    val intentUri: String,
    val label: String,
    val position: Int,
)