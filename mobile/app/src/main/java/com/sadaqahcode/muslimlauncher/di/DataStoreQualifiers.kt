package com.sadaqahcode.muslimlauncher.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserSettingsDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DhikrDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SalawatDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TasbihDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ToolsPreferencesDataStore