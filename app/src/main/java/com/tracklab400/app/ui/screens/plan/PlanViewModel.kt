package com.tracklab400.app.ui.screens.plan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.TrackLabApplication
import com.tracklab400.app.data.model.TrainingPlan
import com.tracklab400.app.data.model.TrainingWeek
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class PlanUiState(
    val plan: TrainingPlan? = null,
    val selectedWeekNumber: Int = 1,
) {
    val selectedWeek: TrainingWeek?
        get() = plan?.weeks?.firstOrNull { it.weekNumber == selectedWeekNumber }
}

class PlanViewModel(application: Application) : AndroidViewModel(application) {

    private val planRepository = (application as TrackLabApplication).container.planRepository

    private val selectedWeek = MutableStateFlow(1)

    val uiState: StateFlow<PlanUiState> = combine(
        planRepository.plan,
        selectedWeek,
    ) { plan, week ->
        val weekNumber = plan?.let { week.coerceIn(1, it.weeks.size) } ?: week
        PlanUiState(plan = plan, selectedWeekNumber = weekNumber)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlanUiState(),
    )

    fun selectWeek(weekNumber: Int) {
        selectedWeek.value = weekNumber
    }
}
