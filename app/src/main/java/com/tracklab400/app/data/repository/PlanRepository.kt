package com.tracklab400.app.data.repository

import com.tracklab400.app.data.local.dao.ProfileDao
import com.tracklab400.app.data.local.dao.SessionStatusDao
import com.tracklab400.app.data.local.db.entity.SessionStatusEntity
import com.tracklab400.app.data.local.db.entity.toDomain
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.TrainingPlan
import com.tracklab400.app.data.plan.TrainingPlanFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PlanRepository(
    private val profileDao: ProfileDao,
    private val sessionStatusDao: SessionStatusDao,
) {

    val plan: Flow<TrainingPlan?> = combine(
        profileDao.observeProfile().map { it?.toDomain() },
        sessionStatusDao.observeAll(),
    ) { profile, statuses ->
        profile?.let { p ->
            TrainingPlanFactory.build(
                profile = p,
                statuses = statuses.associate { (it.weekNumber to it.dayIndex) to it.toDomain() },
            )
        }
    }

    suspend fun markSession(
        weekNumber: Int,
        dayIndex: Int,
        status: SessionStatus,
    ) {
        sessionStatusDao.upsert(
            SessionStatusEntity.from(
                weekNumber = weekNumber,
                dayIndex = dayIndex,
                status = status,
            ),
        )
    }

    suspend fun clearAllStatuses() {
        sessionStatusDao.clearAll()
    }
}
