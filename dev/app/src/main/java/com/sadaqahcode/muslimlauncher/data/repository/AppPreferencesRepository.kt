package com.sadaqahcode.muslimlauncher.data.repository

import com.sadaqahcode.muslimlauncher.data.local.AppPreferenceDao
import com.sadaqahcode.muslimlauncher.data.local.AppPreferenceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferencesRepository(private val dao: AppPreferenceDao) {

    // Key = packageName + userId, 0 = personal, 10 = work
    fun observeAll(): Flow<Map<Pair<String, Int>, AppPreferenceEntity>> =
        dao.observeAll().map { list -> list.associateBy { it.packageName to it.userId } }

    // Work Profile: must pass userId, otherwise personal WhatsApp and work WhatsApp collide
    suspend fun get(packageName: String, userId: Int): AppPreferenceEntity? =
        dao.get(packageName, userId)

    // Old callers - defaults to personal
    suspend fun get(packageName: String): AppPreferenceEntity? =
        dao.get(packageName, 0)

    suspend fun getPinnedCount(): Int = dao.getPinnedCount()

    suspend fun getPinnedCount(userId: Int): Int = dao.getPinnedCount(userId)

    suspend fun getDockedCount(): Int = dao.getDockedCount()

    suspend fun getDockedCount(userId: Int): Int = dao.getDockedCount(userId)

    suspend fun upsert(entity: AppPreferenceEntity) = dao.upsert(entity)
}