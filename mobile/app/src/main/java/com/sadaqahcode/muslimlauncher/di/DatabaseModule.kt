package com.sadaqahcode.muslimlauncher.di

import android.content.Context
import androidx.room.Room
import com.sadaqahcode.muslimlauncher.data.local.AppDatabase
import com.sadaqahcode.muslimlauncher.data.local.AppPreferenceDao
import com.sadaqahcode.muslimlauncher.data.local.ShortcutDao
import com.sadaqahcode.muslimlauncher.data.local.WidgetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "muslim_launcher.db",
    ).build()

    @Provides
    fun provideAppPreferenceDao(db: AppDatabase): AppPreferenceDao = db.appPreferenceDao()

    @Provides
    fun provideWidgetDao(db: AppDatabase): WidgetDao = db.widgetDao()

    @Provides
    fun provideShortcutDao(db: AppDatabase): ShortcutDao = db.shortcutDao()
}