package com.tracklab400.app.ui.screens.exercises

import com.tracklab400.app.data.exercises.CoolDownExercise
import com.tracklab400.app.data.exercises.RunningInterval
import com.tracklab400.app.data.exercises.StrengthExerciseRecord
import com.tracklab400.app.data.exercises.WarmUpExercise

/** Hareket detay sayfasının durumu. */
sealed interface ExerciseDetailUiState {
    data object Loading : ExerciseDetailUiState

    data object NotFound : ExerciseDetailUiState

    data class WarmUp(
        val title: String,
        val context: String?,
        val exercises: List<WarmUpExercise>,
    ) : ExerciseDetailUiState

    data class Running(
        val interval: RunningInterval,
        val canOpenStopwatch: Boolean,
    ) : ExerciseDetailUiState

    data class Strength(
        val planName: String,
        val planCues: String?,
        val record: StrengthExerciseRecord,
    ) : ExerciseDetailUiState

    data class CoolDown(
        val title: String,
        val context: String?,
        val exercises: List<CoolDownExercise>,
    ) : ExerciseDetailUiState
}