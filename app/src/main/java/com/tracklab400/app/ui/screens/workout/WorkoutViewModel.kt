package com.tracklab400.app.ui.screens.workout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.TrackLabApplication
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.WorkoutSession
import com.tracklab400.app.data.repository.PlanRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WorkoutEvent {
    data object SessionMarked : WorkoutEvent
}

class WorkoutViewModel(
    application: Application,
    private val weekNumber: Int,
    private val dayIndex: Int,
) : AndroidViewModel(application) {

    private val planRepository: PlanRepository =
        (application as TrackLabApplication).container.planRepository

    private val _session = MutableStateFlow<WorkoutSession?>(null)
    val session: StateFlow<WorkoutSession?> = _session.asStateFlow()

    private val _events = MutableSharedFlow<WorkoutEvent>()
    val events: SharedFlow<WorkoutEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            planRepository.plan.collect { plan ->
                _session.value = plan?.weeks
                    ?.firstOrNull { it.weekNumber == weekNumber }
                    ?.days?.firstOrNull { it.dayOfWeek.value == dayIndex }
            }
        }
    }

    fun markStatus(status: SessionStatus) {
        viewModelScope.launch {
            planRepository.markSession(weekNumber, dayIndex, status)
            _events.emit(WorkoutEvent.SessionMarked)
        }
    }
}
