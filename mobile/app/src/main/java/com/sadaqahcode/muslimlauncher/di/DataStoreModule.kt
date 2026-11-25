package com.sadaqahcode.muslimlauncher.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.userSettingsDataStore: DataStore<Preferences> by
preferencesDataStore(name = "user_settings")

private val Context.dhikrDataStore: DataStore<Preferences> by
preferencesDataStore(name = "dhikr_cache")

private val Context.tasbihDataStore: DataStore<Preferences> by
preferencesDataStore(name = "tasbih_prefs")

private val Context.toolsPreferencesDataStore: DataStore<Preferences> by
preferencesDataStore(name = "tools_prefs")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    @UserSettingsDataStore
    fun provideUserSettingsDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.userSettingsDataStore

    @Provides
    @Singleton
    @DhikrDataStore
    fun provideDhikrDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dhikrDataStore

    @Provides
    @Singleton
    @TasbihDataStore
    fun provideTasbihDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.tasbihDataStore

    @Provides
    @Singleton
    @ToolsPreferencesDataStore
    fun provideToolsPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.toolsPreferencesDataStore
}