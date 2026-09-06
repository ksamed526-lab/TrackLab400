package com.tracklab400.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tracklab400.app.data.local.db.entity.RepRecordEntity
import com.tracklab400.app.data.local.db.entity.SessionRecordEntity
import com.tracklab400.app.data.local.db.entity.PainReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: SessionRecordEntity): Long

    @Update
    suspend fun update(record: SessionRecordEntity)

    @Query("DELETE FROM session_record WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM session_record WHERE id = :id")
    suspend fun getById(id: Long): SessionRecordEntity?

    @Query("SELECT * FROM session_record ORDER BY completedAt DESC")
    fun observeAll(): Flow<List<SessionRecordEntity>>

    @Query("SELECT * FROM session_record WHERE weekNumber = :weekNumber AND dayOfWeek = :dayOfWeek ORDER BY completedAt DESC LIMIT 1")
    suspend fun getLatestForSession(weekNumber: Int, dayOfWeek: Int): SessionRecordEntity?

    @Query("SELECT COUNT(*) FROM session_record WHERE status = 'COMPLETED'")
    suspend fun countCompleted(): Int

    @Query("SELECT * FROM session_record WHERE weekNumber = :weekNumber ORDER BY dayOfWeek, completedAt")
    suspend fun getByWeek(weekNumber: Int): List<SessionRecordEntity>
}

@Dao
interface RepRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<RepRecordEntity>)

    @Query("SELECT * FROM rep_record WHERE sessionRecordId = :sessionRecordId ORDER BY repIndex")
    suspend fun getBySession(sessionRecordId: Long): List<RepRecordEntity>

    @Query("DELETE FROM rep_record WHERE sessionRecordId = :sessionRecordId")
    suspend fun deleteBySession(sessionRecordId: Long)
}

@Dao
interface PainReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: PainReportEntity): Long

    @Query("SELECT * FROM pain_report WHERE sessionRecordId = :sessionRecordId ORDER BY reportedAt DESC")
    suspend fun getBySession(sessionRecordId: Long): List<PainReportEntity>

    @Query("DELETE FROM pain_report WHERE sessionRecordId = :sessionRecordId")
    suspend fun deleteBySession(sessionRecordId: Long)
}