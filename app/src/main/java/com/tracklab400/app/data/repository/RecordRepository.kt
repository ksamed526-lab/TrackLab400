package com.tracklab400.app.data.repository

import com.tracklab400.app.data.local.dao.PainReportDao
import com.tracklab400.app.data.local.dao.RepRecordDao
import com.tracklab400.app.data.local.dao.SessionRecordDao
import com.tracklab400.app.data.local.db.entity.PainReportEntity
import com.tracklab400.app.data.local.db.entity.RepRecordEntity
import com.tracklab400.app.data.local.db.entity.SessionRecordEntity
import com.tracklab400.app.data.model.PainReport
import com.tracklab400.app.data.model.RepRecord
import com.tracklab400.app.data.model.SessionRecord
import com.tracklab400.app.data.model.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecordRepository(
    private val sessionRecordDao: SessionRecordDao,
    private val repRecordDao: RepRecordDao,
    private val painReportDao: PainReportDao,
) {

    fun observeAll(): Flow<List<SessionRecord>> = sessionRecordDao.observeAll()
        .map { entities -> entities.map { it.withStats() } }

    suspend fun getById(id: Long): SessionRecord? = sessionRecordDao.getById(id)?.withStats()

    suspend fun getByWeek(weekNumber: Int): List<SessionRecord> =
        sessionRecordDao.getByWeek(weekNumber).map { it.withStats() }

    suspend fun getLatestForSession(weekNumber: Int, dayOfWeek: Int): SessionRecord? =
        sessionRecordDao.getLatestForSession(weekNumber, dayOfWeek)?.withStats()

    suspend fun insert(record: SessionRecord): Long = sessionRecordDao.insert(record.toEntity())

    suspend fun update(record: SessionRecord) {
        val entity = record.toEntity()
        sessionRecordDao.update(entity)
    }

    suspend fun delete(id: Long) {
        repRecordDao.deleteBySession(id)
        painReportDao.deleteBySession(id)
        sessionRecordDao.delete(id)
    }

    suspend fun insertReps(reps: List<RepRecord>) = repRecordDao.insertAll(reps.map { it.toEntity() })

    suspend fun getReps(sessionRecordId: Long): List<RepRecord> =
        repRecordDao.getBySession(sessionRecordId).map { it.toDomain() }

    suspend fun deleteReps(sessionRecordId: Long) = repRecordDao.deleteBySession(sessionRecordId)

    suspend fun insertPainReport(report: PainReport): Long = painReportDao.insert(report.toEntity())

    suspend fun getPainReports(sessionRecordId: Long): List<PainReport> =
        painReportDao.getBySession(sessionRecordId).map { it.toDomain() }

    suspend fun countCompleted(): Int = sessionRecordDao.countCompleted()

    fun observeCompletedCount(): Flow<Int> = sessionRecordDao.observeAll()
        .map { entities ->
            entities.count { SessionStatus.valueOf(it.status) == SessionStatus.COMPLETED }
        }

    private suspend fun SessionRecordEntity.withStats(): SessionRecord {
        val base = toDomain()
        return base.copy(
            reps = repRecordDao.getBySession(base.id).map { it.toDomain() },
            painReports = painReportDao.getBySession(base.id).map { it.toDomain() },
        )
    }
}

private fun SessionRecordEntity.toDomain(): SessionRecord = SessionRecord(
    id = id,
    weekNumber = weekNumber,
    dayOfWeek = dayOfWeek,
    sessionKind = sessionKind,
    completedAt = completedAt,
    status = SessionStatus.valueOf(status),
    rpe = rpe,
    sleepHours = sleepHours,
    energyLevel = energyLevel,
    legFeeling = legFeeling,
    painReported = painReported,
    painLocation = painLocation,
    painSeverity = painSeverity,
    notes = notes,
    totalDistanceM = totalDistanceM,
    completedReps = completedReps,
    expectedReps = expectedReps,
    totalTimeMs = totalTimeMs,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun SessionRecord.toEntity(): SessionRecordEntity = SessionRecordEntity(
    id = id,
    weekNumber = weekNumber,
    dayOfWeek = dayOfWeek,
    sessionKind = sessionKind,
    completedAt = completedAt,
    status = status.name,
    rpe = rpe,
    sleepHours = sleepHours,
    energyLevel = energyLevel,
    legFeeling = legFeeling,
    painReported = painReported,
    painLocation = painLocation,
    painSeverity = painSeverity,
    notes = notes,
    totalDistanceM = totalDistanceM,
    completedReps = completedReps,
    expectedReps = expectedReps,
    totalTimeMs = totalTimeMs,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis(),
)

private fun RepRecordEntity.toDomain(): RepRecord = RepRecord(
    id = id,
    sessionRecordId = sessionRecordId,
    repIndex = repIndex,
    distanceM = distanceM,
    targetMinMs = targetMinMs,
    targetMaxMs = targetMaxMs,
    actualMs = actualMs,
    isManual = isManual,
    recordedAt = recordedAt,
)

private fun RepRecord.toEntity(): RepRecordEntity = RepRecordEntity(
    id = id,
    sessionRecordId = sessionRecordId,
    repIndex = repIndex,
    distanceM = distanceM,
    targetMinMs = targetMinMs,
    targetMaxMs = targetMaxMs,
    actualMs = actualMs,
    isManual = isManual,
    recordedAt = recordedAt,
)

private fun PainReportEntity.toDomain(): PainReport = PainReport(
    id = id,
    sessionRecordId = sessionRecordId,
    location = location,
    severity = severity,
    description = description,
    reportedAt = reportedAt,
)

private fun PainReport.toEntity(): PainReportEntity = PainReportEntity(
    id = id,
    sessionRecordId = sessionRecordId,
    location = location,
    severity = severity,
    description = description,
    reportedAt = reportedAt,
)