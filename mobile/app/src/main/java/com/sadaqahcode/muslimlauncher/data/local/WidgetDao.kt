package com.sadaqahcode.muslimlauncher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetDao {

    @Query("SELECT * FROM widget_placements ORDER BY position ASC")
    fun observeAll(): Flow<List<WidgetEntity>>

    @Query("SELECT * FROM widget_placements ORDER BY position ASC")
    suspend fun getAll(): List<WidgetEntity>

    @Query("SELECT COALESCE(MAX(position), -1) FROM widget_placements")
    suspend fun maxPosition(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WidgetEntity)

    @Query("DELETE FROM widget_placements WHERE widgetId = :widgetId")
    suspend fun deleteById(widgetId: Int)

    @Query("UPDATE widget_placements SET position = :position WHERE widgetId = :widgetId")
    suspend fun updatePosition(widgetId: Int, position: Int)

    @Query("UPDATE widget_placements SET heightDp = :heightDp WHERE widgetId = :widgetId")
    suspend fun updateHeight(widgetId: Int, heightDp: Int)

    @Query("DELETE FROM widget_placements WHERE providerName NOT IN (:installed)")
    suspend fun deleteMissingProviders(installed: List<String>)

    @Query("SELECT * FROM widget_placements WHERE providerName NOT IN (:installed)")
    suspend fun findMissingProviders(installed: List<String>): List<WidgetEntity>
}