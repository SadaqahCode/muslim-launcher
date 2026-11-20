package com.sadaqahcode.muslimlauncher.data.repository

import com.sadaqahcode.muslimlauncher.data.local.ShortcutDao
import com.sadaqahcode.muslimlauncher.data.local.ShortcutEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShortcutRepository @Inject constructor(
    private val dao: ShortcutDao,
) {

    fun observeAll(): Flow<List<ShortcutEntity>> = dao.observeAll()

    suspend fun getAll(): List<ShortcutEntity> = dao.getAll()

    suspend fun add(
        id: String,
        packageName: String,
        activityName: String,
        intentUri: String,
        label: String,
    ) {
        dao.upsert(
            ShortcutEntity(
                id = id,
                packageName = packageName,
                activityName = activityName,
                intentUri = intentUri,
                label = label,
                position = dao.maxPosition() + 1,
            )
        )
    }

    suspend fun remove(id: String) = dao.deleteById(id)

    suspend fun updatePosition(id: String, position: Int) =
        dao.updatePosition(id, position)
}