package com.sadaqahcode.muslimlauncher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppPreferenceDao {

    @Query("SELECT * FROM app_preferences")
    fun observeAll(): Flow<List<AppPreferenceEntity>>

    // ── Work Profile: PK is now (packageName, userId) ──
    // 0 = personal, 10 = work, 11 = private space
    @Query("SELECT * FROM app_preferences WHERE packageName = :packageName AND userId = :userId")
    suspend fun get(packageName: String, userId: Int): AppPreferenceEntity?

    // Old callers - defaults to personal, so old code still compiles
    suspend fun get(packageName: String): AppPreferenceEntity? = get(packageName, 0)

    @Query("SELECT COUNT(*) FROM app_preferences WHERE isPinned = 1")
    suspend fun getPinnedCount(): Int

    @Query("SELECT COUNT(*) FROM app_preferences WHERE isPinned = 1 AND userId = :userId")
    suspend fun getPinnedCount(userId: Int): Int

    @Query("SELECT COUNT(*) FROM app_preferences WHERE isDocked = 1")
    suspend fun getDockedCount(): Int

    @Query("SELECT COUNT(*) FROM app_preferences WHERE isDocked = 1 AND userId = :userId")
    suspend fun getDockedCount(userId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AppPreferenceEntity)
}