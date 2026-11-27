package com.sadaqahcode.muslimlauncher.data.local.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sadaqahcode.muslimlauncher.data.local.entity.SalawatCacheEntity
import com.sadaqahcode.muslimlauncher.data.local.mapper.SalawatCacheMapper
import com.sadaqahcode.muslimlauncher.di.SalawatDataStore
import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.Salawat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalawatLocalDataSource @Inject constructor(
    @SalawatDataStore private val dataStore: DataStore<Preferences>,
    private val json: Json,
    private val cacheMapper: SalawatCacheMapper,
) {
    companion object {
        private val CACHE_KEY = stringPreferencesKey("salawat_cache")
        private val LAST_UPDATED_KEY = longPreferencesKey("salawat_cache_last_updated")
    }

    fun getCachedContent(): Flow<List<Salawat>?> =
        dataStore.data.map { prefs ->
            prefs[CACHE_KEY]?.let { raw ->
                try {
                    cacheMapper.toDomain(json.decodeFromString<SalawatCacheEntity>(raw))
                } catch (e: Exception) {
                    null
                }
            }
        }

    suspend fun saveCache(items: List<Salawat>) {
        val entity = cacheMapper.toEntity(items)
        val raw = json.encodeToString(SalawatCacheEntity.serializer(), entity)
        dataStore.edit { prefs ->
            prefs[CACHE_KEY] = raw
            prefs[LAST_UPDATED_KEY] = entity.lastUpdated
        }
    }

    suspend fun clearCache() {
        dataStore.edit { prefs ->
            prefs.remove(CACHE_KEY)
            prefs.remove(LAST_UPDATED_KEY)
        }
    }

    suspend fun getLastUpdated(): Long? =
        dataStore.data.map { it[LAST_UPDATED_KEY] }.first()

    suspend fun getCacheAgeSeconds(): Long? {
        val lastUpdated = getLastUpdated() ?: return null
        return (System.currentTimeMillis() - lastUpdated) / 1000
    }

    suspend fun isCacheValid(maxAgeSeconds: Long): Boolean {
        if (maxAgeSeconds < 0) return getLastUpdated() != null

        val lastUpdated = getLastUpdated() ?: return false
        if (maxAgeSeconds == 0L) return false

        val ageSeconds = (System.currentTimeMillis() - lastUpdated) / 1000
        return ageSeconds < maxAgeSeconds
    }
}