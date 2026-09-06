package com.tracklab400.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tracklab400.app.data.local.db.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profile WHERE id = 1 LIMIT 1")
    fun observeProfile(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profile WHERE id = 1 LIMIT 1")
    suspend fun getProfile(): ProfileEntity?

    @Upsert
    suspend fun upsertProfile(profile: ProfileEntity)

    @Query("DELETE FROM profile")
    suspend fun deleteAll()
}
