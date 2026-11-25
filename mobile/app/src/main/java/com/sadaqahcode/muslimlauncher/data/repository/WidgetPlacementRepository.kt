package com.sadaqahcode.muslimlauncher.data.repository

import com.sadaqahcode.muslimlauncher.data.local.WidgetDao
import com.sadaqahcode.muslimlauncher.data.local.WidgetEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetPlacementRepository @Inject constructor(
    private val dao: WidgetDao,
) {

    fun observeAll(): Flow<List<WidgetEntity>> = dao.observeAll()

    suspend fun getAll(): List<WidgetEntity> = dao.getAll()

    suspend fun nextPosition(): Int = dao.maxPosition() + 1

    suspend fun add(widgetId: Int, providerName: String) {
        dao.upsert(
            WidgetEntity(
                widgetId = widgetId,
                providerName = providerName,
                position = nextPosition(),
                heightDp = 0,
            )
        )
    }

    suspend fun remove(widgetId: Int) {
        dao.deleteById(widgetId)
    }

    suspend fun updatePosition(widgetId: Int, position: Int) {
        dao.updatePosition(widgetId, position)
    }

    suspend fun updateHeight(widgetId: Int, heightDp: Int) {
        dao.updateHeight(widgetId, heightDp)
    }

    suspend fun findOrphans(installed: List<String>): List<WidgetEntity> =
        dao.findMissingProviders(installed)

    suspend fun deleteOrphans(installed: List<String>) {
        dao.deleteMissingProviders(installed)
    }
}