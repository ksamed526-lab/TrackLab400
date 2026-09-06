package com.tracklab400.app.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tracklab400.app.R
import com.tracklab400.app.data.model.SessionRecord
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.TimeUtils
import com.tracklab400.app.ui.common.displayName
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabEmpty
import com.tracklab400.app.ui.components.TrackLabTag
import com.tracklab400.app.ui.theme.TrackLabSpacing
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onFilterStatusChange: (SessionStatus?) -> Unit,
    onSearchChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    onDelete: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var deleteTarget by remember { mutableStateOf<SessionRecord?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = TrackLabSpacing.md),
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.history_search_hint)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
            )

            Spacer(Modifier.height(TrackLabSpacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatusFilterChip(
                    labelRes = R.string.history_filter_all,
                    selected = uiState.filterStatus == null,
                    onClick = { onFilterStatusChange(null) },
                )
                StatusFilterChip(
                    labelRes = R.string.action_mark_completed,
                    selected = uiState.filterStatus == SessionStatus.COMPLETED,
                    onClick = { onFilterStatusChange(SessionStatus.COMPLETED) },
                )
                StatusFilterChip(
                    labelRes = R.string.action_mark_partial,
                    selected = uiState.filterStatus == SessionStatus.PARTIAL,
                    onClick = { onFilterStatusChange(SessionStatus.PARTIAL) },
                )
                StatusFilterChip(
                    labelRes = R.string.action_skip,
                    selected = uiState.filterStatus == SessionStatus.SKIPPED,
                    onClick = { onFilterStatusChange(SessionStatus.SKIPPED) },
                )
            }

            val hasActiveFilters =
                uiState.filterStatus != null || uiState.searchQuery.isNotBlank()
            if (hasActiveFilters) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onClearFilters) {
                        Text(stringResource(R.string.history_clear_filters))
                    }
                }
            }

            Spacer(Modifier.height(TrackLabSpacing.sm))

            val records = uiState.filteredRecords
            if (records.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    TrackLabEmpty(
                        title = stringResource(R.string.history_empty_title),
                        description = stringResource(R.string.history_empty_desc),
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(TrackLabSpacing.sm),
                ) {
                    items(records, key = { it.id }) { record ->
                        HistoryRecordCard(
                            record = record,
                            onDelete = { deleteTarget = record },
                        )
                    }
                    item { Spacer(Modifier.height(TrackLabSpacing.lg)) }
                }
            }
        }
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(stringResource(R.string.history_delete_title)) },
            text = { Text(stringResource(R.string.history_delete_desc)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(target.id)
                    deleteTarget = null
                }) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun StatusFilterChip(
    labelRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(stringResource(labelRes)) },
    )
}

@Composable
private fun HistoryRecordCard(
    record: SessionRecord,
    onDelete: () -> Unit,
) {
    TrackLabCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        R.string.history_session_info,
                        record.weekNumber,
                        sessionKindLabel(record.sessionKind),
                        record.completedReps,
                        record.expectedReps,
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = historyDate(record.completedAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrackLabTag(
                        text = record.status.displayName(),
                        containerColor = statusContainer(record.status),
                        contentColor = statusContent(record.status),
                    )
                    if (record.painReported) {
                        TrackLabTag(
                            text = stringResource(R.string.comp_pain_title),
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = historyStatsLine(record),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

private fun sessionKindLabel(kind: String): String = when (kind) {
    "HIZLANMA_KUVVET_A" -> "Hızlanma + Kuvvet A"
    "TEMPO" -> "Tempo"
    "OZEL_DAYANIKLILIK" -> "Özel Dayanıklılık"
    "MAKS_HIZ_KUVVET_B" -> "Maks Hız + Kuvvet B"
    "TEST_YARIS" -> "Test / Yarış"
    "DINLENME" -> "Dinlenme"
    "HAFIF_TOPARLANMA" -> "Hafif Toparlanma"
    else -> kind
}

private fun historyStatsLine(record: SessionRecord): String {
    val parts = mutableListOf<String>()
    val avg = record.averageRepMs
    val best = record.bestRepMs
    if (avg != null) parts.add("ort ${TimeUtils.formatSeconds(avg)} sn")
    if (best != null) parts.add("en iyi ${TimeUtils.formatSeconds(best)} sn")
    record.avgDeviationMs?.let { parts.add("sapma ${deviationText(it)}") }
    if (record.rpe != null) parts.add("Zorluk ${record.rpe}")
    return if (parts.isEmpty()) "-" else parts.joinToString(" · ")
}

private fun deviationText(deviationMs: Long): String {
    val sign = if (deviationMs > 0) "+" else "−"
    return "$sign${TimeUtils.formatSeconds(kotlin.math.abs(deviationMs))} sn"
}

private fun historyDate(millis: Long): String {
    val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
    return date.format(
        DateTimeFormatter.ofPattern("d MMM HH:mm", Locale("tr", "TR")),
    )
}

@Composable
private fun statusContainer(status: SessionStatus) = when (status) {
    SessionStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
    SessionStatus.PARTIAL -> MaterialTheme.colorScheme.tertiaryContainer
    SessionStatus.SKIPPED -> MaterialTheme.colorScheme.errorContainer
    SessionStatus.PLANNED -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun statusContent(status: SessionStatus) = when (status) {
    SessionStatus.COMPLETED -> MaterialTheme.colorScheme.onPrimaryContainer
    SessionStatus.PARTIAL -> MaterialTheme.colorScheme.onTertiaryContainer
    SessionStatus.SKIPPED -> MaterialTheme.colorScheme.onErrorContainer
    SessionStatus.PLANNED -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    MaterialTheme {
        HistoryScreen(
            uiState = HistoryUiState(),
            onFilterStatusChange = {},
            onSearchChange = {},
            onClearFilters = {},
            onDelete = {},
            onBack = {},
        )
    }
}