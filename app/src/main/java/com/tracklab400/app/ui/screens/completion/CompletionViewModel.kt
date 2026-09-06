package com.tracklab400.app.ui.screens.completion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.TrackLabApplication
import com.tracklab400.app.data.model.PainReport
import com.tracklab400.app.data.model.RepRecord
import com.tracklab400.app.data.model.SessionRecord
import com.tracklab400.app.data.model.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CompletionUiState(
    val weekNumber: Int = 0,
    val dayIndex: Int = 0,
    val sessionTitle: String = "",
    val sessionKind: String = "",
    val expectedReps: Int = 0,
    val distanceM: Int = 0,
    val targetMinMs: Long? = null,
    val targetMaxMs: Long? = null,
    val reps: List<RepRecord> = emptyList(),
    val totalTimeMs: Long = 0,
    val rpe: Int? = null,
    val sleepHours: Double? = null,
    val energyLevel: Int? = null,
    val legFeeling: Int? = null,
    val hasPain: Boolean = false,
    val painLocation: String = "",
    val painSeverity: Int = 5,
    val painDescription: String = "",
    val notes: String = "",
    val status: SessionStatus = SessionStatus.COMPLETED,
    val isSaving: Boolean = false,
    val showPainWarning: Boolean = false,
) {
    val completedReps: Int
        get() = reps.size

    val isLastRep: Boolean
        get() = expectedReps > 0 && reps.size >= expectedReps

    val completionPercent: Float
        get() = if (expectedReps > 0) reps.size.toFloat() / expectedReps else 0f
}

class CompletionViewModel(
    application: Application,
    private val weekNumber: Int,
    private val dayIndex: Int,
) : AndroidViewModel(application) {

    private val container = (application as TrackLabApplication).container
    private val recordRepository = container.recordRepository
    private val planRepository = container.planRepository

    private val _uiState = MutableStateFlow(
        CompletionUiState(
            weekNumber = weekNumber,
            dayIndex = dayIndex,
        ),
    )
    val uiState: StateFlow<CompletionUiState> = _uiState.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    init {
        viewModelScope.launch {
            val plan = planRepository.plan.first()
            val session = plan?.weeks
                ?.firstOrNull { it.weekNumber == weekNumber }
                ?.days?.firstOrNull { it.dayOfWeek.value == dayIndex }
            if (session != null) {
                val main = session.exercises.firstOrNull { it.distanceM != null }
                _uiState.update { state ->
                    state.copy(
                        sessionTitle = session.title,
                        sessionKind = session.kind.name,
                        expectedReps = main?.reps ?: state.expectedReps,
                        distanceM = main?.distanceM ?: state.distanceM,
                        targetMinMs = main?.targetMinMs ?: state.targetMinMs,
                        targetMaxMs = main?.targetMaxMs ?: state.targetMaxMs,
                    )
                }
            }
        }
    }

    fun addRep(actualMs: Long) {
        _uiState.update { state ->
            val rep = RepRecord(
                repIndex = state.reps.size + 1,
                distanceM = state.distanceM,
                targetMinMs = state.targetMinMs,
                targetMaxMs = state.targetMaxMs,
                actualMs = actualMs,
                recordedAt = System.currentTimeMillis(),
            )
            state.copy(
                reps = state.reps + rep,
                totalTimeMs = state.totalTimeMs + actualMs,
            )
        }
    }

    fun undoLastRep() {
        _uiState.update { state ->
            if (state.reps.isEmpty()) return@update state
            val last = state.reps.last()
            state.copy(
                reps = state.reps.dropLast(1),
                totalTimeMs = (state.totalTimeMs - last.actualMs).coerceAtLeast(0L),
            )
        }
    }

    fun setRpe(value: Int) {
        _uiState.update { it.copy(rpe = value) }
    }

    fun setSleepHours(value: Double) {
        _uiState.update { it.copy(sleepHours = value) }
    }

    fun setEnergyLevel(value: Int) {
        _uiState.update { it.copy(energyLevel = value) }
    }

    fun setLegFeeling(value: Int) {
        _uiState.update { it.copy(legFeeling = value) }
    }

    fun setPain(location: String, severity: Int, description: String) {
        _uiState.update {
            it.copy(
                hasPain = true,
                painLocation = location,
                painSeverity = severity,
                painDescription = description,
            )
        }
    }

    fun clearPain() {
        _uiState.update { it.copy(hasPain = false) }
    }

    fun setNotes(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun dismissPainWarning() {
        _uiState.update { it.copy(showPainWarning = false) }
    }

    fun showPainWarning() {
        _uiState.update { it.copy(showPainWarning = true) }
    }

    fun save() {
        val state = _uiState.value
        if (state.isSaving) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val finalStatus = when {
                state.status == SessionStatus.SKIPPED -> SessionStatus.SKIPPED
                state.expectedReps > 0 && state.reps.size >= state.expectedReps ->
                    SessionStatus.COMPLETED
                else -> SessionStatus.PARTIAL
            }
            val now = System.currentTimeMillis()
            val record = SessionRecord(
                weekNumber = state.weekNumber,
                dayOfWeek = state.dayIndex,
                sessionKind = state.sessionKind,
                completedAt = now,
                status = finalStatus,
                rpe = state.rpe,
                sleepHours = state.sleepHours,
                energyLevel = state.energyLevel,
                legFeeling = state.legFeeling,
                painReported = state.hasPain,
                painLocation = state.painLocation.ifBlank { null },
                painSeverity = if (state.hasPain) state.painSeverity else null,
                notes = state.notes.ifBlank { null },
                totalDistanceM = state.distanceM,
                completedReps = state.reps.size,
                expectedReps = state.expectedReps,
                totalTimeMs = state.totalTimeMs,
                createdAt = now,
                updatedAt = now,
            )
            val id = recordRepository.insert(record)
            recordRepository.insertReps(
                state.reps.map { it.copy(sessionRecordId = id) },
            )
            if (state.hasPain && state.painLocation.isNotBlank()) {
                recordRepository.insertPainReport(
                    PainReport(
                        sessionRecordId = id,
                        location = state.painLocation,
                        severity = state.painSeverity,
                        description = state.painDescription.ifBlank { null },
                        reportedAt = now,
                    ),
                )
            }
            planRepository.markSession(
                weekNumber = state.weekNumber,
                dayIndex = state.dayIndex,
                status = finalStatus,
            )
            _saved.value = true
        }
    }
}