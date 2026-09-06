package com.tracklab400.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.TrackLabApplication
import com.tracklab400.app.data.model.TrainingPlan
import com.tracklab400.app.data.model.WorkoutSession
import com.tracklab400.app.data.plan.TrainingPlanFactory
import com.tracklab400.app.data.repository.PlanRepository
import com.tracklab400.app.data.repository.ProfileRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val nickname: String? = null,
    val currentTimeMs: Long? = null,
    val targetTimeMs: Long? = null,
    val targetDistance: Int = 400,
    val plan: TrainingPlan? = null,
    val currentWeekNumber: Int = 1,
    val weekCount: Int = 8,
    val completionPercent: Float = 0f,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val daysRemaining: Long? = null,
    val todaySession: WorkoutSession? = null,
    val nextSession: WorkoutSession? = null,
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val profileRepository: ProfileRepository =
        (application as TrackLabApplication).container.profileRepository
    private val planRepository: PlanRepository =
        (application as TrackLabApplication).container.planRepository

    val uiState: StateFlow<HomeUiState> = combine(
        profileRepository.profile,
        planRepository.plan,
    ) { profile, plan ->
        buildUiState(profile?.nickname, profile, plan)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    private fun buildUiState(
        nickname: String?,
        profile: com.tracklab400.app.data.model.Profile?,
        plan: TrainingPlan?,
    ): HomeUiState {
        if (plan == null) {
            return HomeUiState(
                nickname = nickname,
                currentTimeMs = profile?.current400mMs,
                targetTimeMs = profile?.targetTimeMs,
                targetDistance = profile?.targetDistanceM ?: 400,
            )
        }
        val (completed, total) = TrainingPlanFactory.completionStats(plan)
        val today = LocalDate.now().dayOfWeek
        val todaySession = TrainingPlanFactory.sessionFor(plan, today)
        val next = TrainingPlanFactory.nextSession(plan, today)
        val days = profile?.let {
            TrainingPlanFactory.daysRemaining(
                startDateMillis = it.startDateMillis,
                prepWeeks = it.prepWeeks,
                now = System.currentTimeMillis(),
            )
        }
        return HomeUiState(
            nickname = nickname,
            currentTimeMs = profile?.current400mMs,
            targetTimeMs = profile?.targetTimeMs,
            targetDistance = profile?.targetDistanceM ?: 400,
            plan = plan,
            currentWeekNumber = plan.currentWeekNumber,
            weekCount = plan.weeks.size,
            completionPercent = if (total == 0) 0f else completed.toFloat() / total,
            completedCount = completed,
            totalCount = total,
            daysRemaining = days,
            todaySession = todaySession,
            nextSession = next,
        )
    }
}
