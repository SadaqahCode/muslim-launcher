package com.sadaqahcode.muslimlauncher.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_placements")
data class WidgetEntity(
    @PrimaryKey val widgetId: Int,
    val providerName: String,
    val position: Int,
    val heightDp: Int = 0,
)