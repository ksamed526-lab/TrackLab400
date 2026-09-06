package com.tracklab400.app.ui.screens.progress

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.TrackLabApplication
import com.tracklab400.app.data.stats.ProgressDateRange
import com.tracklab400.app.data.stats.ProgressStatsCalculator
import com.tracklab400.app.data.stats.ProgressUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as TrackLabApplication).container

    private val recordRepository = container.recordRepository
    private val profileRepository = container.profileRepository

    private val _range = MutableStateFlow(ProgressDateRange.ALL)

    val uiState: StateFlow<ProgressUiState> = combine(
        recordRepository.observeAll(),
        profileRepository.profile,
        _range,
    ) { records, profile, range ->
        ProgressStatsCalculator.compute(
            records = records,
            profile = profile,
            range = range,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressUiState(),
    )

    fun setRange(range: ProgressDateRange) {
        _range.value = range
    }
}