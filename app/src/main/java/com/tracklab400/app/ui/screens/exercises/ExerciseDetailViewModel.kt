package com.tracklab400.app.ui.screens.exercises

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.TrackLabApplication
import com.tracklab400.app.data.exercises.CoolDownCatalog
import com.tracklab400.app.data.exercises.RunningIntervalFactory
import com.tracklab400.app.data.exercises.StrengthCatalog
import com.tracklab400.app.data.exercises.WarmUpCatalog
import com.tracklab400.app.data.model.ExerciseType
import com.tracklab400.app.data.model.WorkoutExercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Hareket detay sayfası. İki giriş yolunu destekler:
 * - Seansa bağlı: (weekNumber, dayIndex, blockIndex) ile seans içindeki bloğu çözer.
 * - Kütüphane: exerciseId ("warmup_"/"strength_"/"cooldown_" ön ekleri) ile katalogdan çözer.
 */
class ExerciseDetailViewModel(
    application: Application,
    private val weekNumber: Int? = null,
    private val dayIndex: Int? = null,
    private val blockIndex: Int? = null,
    private val exerciseId: String? = null,
) : AndroidViewModel(application) {

    private val planRepository =
        (application as TrackLabApplication).container.planRepository

    private val _uiState = MutableStateFlow<ExerciseDetailUiState>(ExerciseDetailUiState.Loading)
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    init {
        if (weekNumber != null && dayIndex != null && blockIndex != null) {
            viewModelScope.launch {
                val plan = planRepository.plan.first { it != null } ?: return@launch
                val session = plan.weeks
                    .firstOrNull { it.weekNumber == weekNumber }
                    ?.days?.firstOrNull { it.dayOfWeek.value == dayIndex }
                val exercise = session?.exercises?.getOrNull(blockIndex)
                _uiState.value = if (exercise == null) {
                    ExerciseDetailUiState.NotFound
                } else {
                    resolveSessionExercise(exercise, session.kind)
                }
            }
        } else if (exerciseId != null) {
            _uiState.value = resolveLibraryExercise(exerciseId)
        } else {
            _uiState.value = ExerciseDetailUiState.NotFound
        }
    }

    private fun resolveSessionExercise(
        exercise: WorkoutExercise,
        kind: com.tracklab400.app.data.model.SessionKind,
    ): ExerciseDetailUiState = when (exercise.type) {
        ExerciseType.WARMUP -> {
            val drills = WarmUpCatalog.blocksForPlanName(exercise.name).ifEmpty { WarmUpCatalog.all }
            ExerciseDetailUiState.WarmUp(
                title = exercise.name,
                context = exercise.cues,
                exercises = drills,
            )
        }
        ExerciseType.RUN -> ExerciseDetailUiState.Running(
            interval = RunningIntervalFactory.from(exercise, kind),
            canOpenStopwatch = exercise.distanceM != null,
        )
        ExerciseType.STRENGTH -> {
            val record = StrengthCatalog.byPlanName(exercise.name)
            if (record == null) {
                ExerciseDetailUiState.NotFound
            } else {
                ExerciseDetailUiState.Strength(
                    planName = exercise.name,
                    planCues = exercise.cues,
                    record = record.copy(
                        weight = null,
                        completedSets = 0,
                        perSetNotes = mutableListOf(),
                    ),
                )
            }
        }
        ExerciseType.NOTE -> ExerciseDetailUiState.NotFound
    }

    private fun resolveLibraryExercise(id: String): ExerciseDetailUiState = when {
        id.startsWith("warmup_") -> {
            val entry = WarmUpCatalog.findById(id)
            if (entry == null) {
                ExerciseDetailUiState.NotFound
            } else {
                ExerciseDetailUiState.WarmUp(title = entry.name, context = null, exercises = listOf(entry))
            }
        }
        id.startsWith("strength_") -> {
            val record = StrengthCatalog.findById(id)
            if (record == null) {
                ExerciseDetailUiState.NotFound
            } else {
                ExerciseDetailUiState.Strength(
                    planName = record.name,
                    planCues = null,
                    record = record.copy(
                        weight = null,
                        completedSets = 0,
                        perSetNotes = mutableListOf(),
                    ),
                )
            }
        }
        id.startsWith("cooldown_") -> {
            val entry = CoolDownCatalog.findById(id)
            if (entry == null) {
                ExerciseDetailUiState.NotFound
            } else {
                ExerciseDetailUiState.CoolDown(
                    title = entry.name,
                    context = null,
                    exercises = listOf(entry),
                )
            }
        }
        else -> ExerciseDetailUiState.NotFound
    }
}