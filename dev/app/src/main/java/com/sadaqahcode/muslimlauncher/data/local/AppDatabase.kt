package com.sadaqahcode.muslimlauncher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AppPreferenceEntity::class, WidgetEntity::class, ShortcutEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appPreferenceDao(): AppPreferenceDao
    abstract fun widgetDao(): WidgetDao
    abstract fun shortcutDao(): ShortcutDao
}