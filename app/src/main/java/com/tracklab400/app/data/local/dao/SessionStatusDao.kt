package com.tracklab400.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tracklab400.app.data.local.db.entity.SessionStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionStatusDao {

    @Query("SELECT * FROM session_status")
    fun observeAll(): Flow<List<SessionStatusEntity>>

    @Upsert
    suspend fun upsert(status: SessionStatusEntity)

    @Query("DELETE FROM session_status")
    suspend fun clearAll()
}
