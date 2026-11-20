package com.sadaqahcode.muslimlauncher.data.repository.tools

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.sadaqahcode.muslimlauncher.di.ToolsPreferencesDataStore
import com.sadaqahcode.muslimlauncher.domain.model.ToolId
import com.sadaqahcode.muslimlauncher.domain.model.ToolPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolPreferencesRepository @Inject constructor(
    @ToolsPreferencesDataStore private val dataStore: DataStore<Preferences>,
) {
    private val enabledKey = stringSetPreferencesKey("tools.enabled_overrides")
    private val orderKey   = stringPreferencesKey("tools.order")

    val prefs: Flow<ToolPreferences> = dataStore.data.map { p ->
        ToolPreferences(
            enabledOverrides = p[enabledKey].orEmpty()
                .mapNotNull { entry ->
                    val (k, v) = entry.split('=', limit = 2).takeIf { it.size == 2 } ?: return@mapNotNull null
                    ToolId.fromKey(k)?.let { it to (v == "true") }
                }
                .toMap(),
            order = p[orderKey].orEmpty()
                .split(',')
                .mapNotNull { it.takeIf { it.isNotBlank() }?.let(ToolId::fromKey) },
        )
    }

    suspend fun setEnabled(id: ToolId, enabled: Boolean) {
        dataStore.edit { p ->
            val current = p[enabledKey].orEmpty()
                .filterNot { it.startsWith("${id.key}=") }
                .toMutableSet()
            current += "${id.key}=$enabled"
            p[enabledKey] = current
        }
    }

    suspend fun setOrder(ids: List<ToolId>) {
        dataStore.edit { it[orderKey] = ids.joinToString(",") { id -> id.key } }
    }

    suspend fun reset() {
        dataStore.edit { it.remove(enabledKey); it.remove(orderKey) }
    }
}