package com.tracklab400.app.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.TrackLabApplication
import com.tracklab400.app.data.model.SessionRecord
import com.tracklab400.app.data.model.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val records: List<SessionRecord> = emptyList(),
    val filterStatus: SessionStatus? = null,
    val filterWeek: Int? = null,
    val searchQuery: String = "",
) {
    val filteredRecords: List<SessionRecord>
        get() = records
            .filter { record ->
                (filterStatus == null || record.status == filterStatus) &&
                    (filterWeek == null || record.weekNumber == filterWeek) &&
                    (searchQuery.isBlank() || matchesSearch(record))
            }
            .sortedByDescending { it.completedAt }

    private fun matchesSearch(record: SessionRecord): Boolean {
        val query = searchQuery.trim()
        if (record.notes?.contains(query, ignoreCase = true) == true) return true
        if (record.sessionKind.contains(query, ignoreCase = true)) return true
        return "hafta ${record.weekNumber}".contains(query, ignoreCase = true)
    }
}

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val recordRepository =
        (application as TrackLabApplication).container.recordRepository

    private val _filterStatus = MutableStateFlow<SessionStatus?>(null)
    private val _filterWeek = MutableStateFlow<Int?>(null)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<HistoryUiState> = combine(
        recordRepository.observeAll(),
        _filterStatus,
        _filterWeek,
        _searchQuery,
    ) { records, status, week, query ->
        HistoryUiState(
            records = records,
            filterStatus = status,
            filterWeek = week,
            searchQuery = query,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState(),
    )

    fun setFilterStatus(status: SessionStatus?) {
        _filterStatus.value = status
    }

    fun setFilterWeek(week: Int?) {
        _filterWeek.value = week
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearFilters() {
        _filterStatus.value = null
        _filterWeek.value = null
        _searchQuery.value = ""
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            recordRepository.delete(id)
        }
    }
}