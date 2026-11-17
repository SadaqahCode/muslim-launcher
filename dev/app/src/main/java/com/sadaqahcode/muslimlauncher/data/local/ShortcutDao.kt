package com.sadaqahcode.muslimlauncher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShortcutDao {

    @Query("SELECT * FROM shortcut_placements ORDER BY position ASC")
    fun observeAll(): Flow<List<ShortcutEntity>>

    @Query("SELECT * FROM shortcut_placements ORDER BY position ASC")
    suspend fun getAll(): List<ShortcutEntity>

    @Query("SELECT COALESCE(MAX(position), -1) FROM shortcut_placements")
    suspend fun maxPosition(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ShortcutEntity)

    @Query("DELETE FROM shortcut_placements WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE shortcut_placements SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: String, position: Int)
}