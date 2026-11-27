package com.sadaqahcode.muslimlauncher.data.local.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.sadaqahcode.muslimlauncher.di.SalawatDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalawatPrefsDataStore @Inject constructor(
    @SalawatDataStore private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val FavoriteIds = stringSetPreferencesKey("favorite_salawat_ids")
        val LastSelectedId = intPreferencesKey("last_selected_salawat_id")
    }

    private val prefsFlow: Flow<Preferences> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }

    val favoriteIds: Flow<Set<Int>> = prefsFlow
        .map { p ->
            p[Keys.FavoriteIds]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: emptySet()
        }
        .distinctUntilChanged()

    val lastSelectedId: Flow<Int?> = prefsFlow
        .map { it[Keys.LastSelectedId] }
        .distinctUntilChanged()

    suspend fun toggleFavorite(id: Int) {
        dataStore.edit { p ->
            val current = p[Keys.FavoriteIds] ?: emptySet()
            val key = id.toString()
            p[Keys.FavoriteIds] = if (key in current) current - key else current + key
        }
    }

    suspend fun saveLastSelected(id: Int) {
        dataStore.edit { it[Keys.LastSelectedId] = id }
    }
}