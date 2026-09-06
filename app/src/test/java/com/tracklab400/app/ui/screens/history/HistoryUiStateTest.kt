package com.tracklab400.app.ui.screens.history

import com.tracklab400.app.data.model.SessionRecord
import com.tracklab400.app.data.model.SessionStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryUiStateTest {

    private fun record(
        id: Long,
        week: Int,
        kind: String,
        completedAt: Long,
        status: SessionStatus,
        notes: String? = null,
    ) = SessionRecord(
        id = id,
        weekNumber = week,
        dayOfWeek = 4,
        sessionKind = kind,
        completedAt = completedAt,
        status = status,
        notes = notes,
    )

    private val records = listOf(
        record(1, 2, "TEMPO", 3_000L, SessionStatus.COMPLETED),
        record(2, 1, "HIZLANMA_KUVVET_A", 1_000L, SessionStatus.PARTIAL),
        record(3, 2, "OZEL_DAYANIKLILIK", 2_000L, SessionStatus.SKIPPED),
    )

    @Test
    fun noFilters_returnsAllSortedDescByDate() {
        val state = HistoryUiState(records = records)
        assertEquals(listOf(1L, 3L, 2L), state.filteredRecords.map { it.id })
    }

    @Test
    fun statusFilter_filtersByStatus() {
        val state = HistoryUiState(
            records = records,
            filterStatus = SessionStatus.COMPLETED,
        )
        assertEquals(listOf(1L), state.filteredRecords.map { it.id })
    }

    @Test
    fun weekFilter_filtersByWeek() {
        val state = HistoryUiState(
            records = records,
            filterWeek = 2,
        )
        assertEquals(listOf(1L, 3L), state.filteredRecords.map { it.id })
    }

    @Test
    fun searchQuery_matchesSessionKind() {
        val state = HistoryUiState(
            records = records,
            searchQuery = "tempo",
        )
        assertEquals(listOf(1L), state.filteredRecords.map { it.id })
    }

    @Test
    fun searchQuery_matchesNotes() {
        val state = HistoryUiState(
            records = listOf(record(5, 1, "TEMPO", 4_000L, SessionStatus.COMPLETED, notes = "bacaklar ağrıdı")),
            searchQuery = "ağrıdı",
        )
        assertEquals(listOf(5L), state.filteredRecords.map { it.id })
    }

    @Test
    fun searchQuery_matchesWeekKeyword() {
        val state = HistoryUiState(
            records = records,
            searchQuery = "hafta 1",
        )
        assertEquals(listOf(2L), state.filteredRecords.map { it.id })
    }

    @Test
    fun combinedFilters_intersect() {
        val state = HistoryUiState(
            records = records,
            filterStatus = SessionStatus.SKIPPED,
            filterWeek = 2,
        )
        assertEquals(listOf(3L), state.filteredRecords.map { it.id })
    }

    @Test
    fun emptyState_returnsEmpty() {
        val state = HistoryUiState(records = emptyList())
        assertEquals(emptyList<Long>(), state.filteredRecords.map { it.id })
    }
}