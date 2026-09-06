package com.tracklab400.app.ui.screens.completion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracklab400.app.R
import com.tracklab400.app.data.model.RepRecord
import com.tracklab400.app.data.model.TimeUtils
import com.tracklab400.app.ui.components.TrackLabAccentButton
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabSectionHeader
import com.tracklab400.app.ui.components.TrackLabSlider
import com.tracklab400.app.ui.components.TrackLabTag
import com.tracklab400.app.ui.theme.Lime400
import com.tracklab400.app.ui.theme.TrackLabSpacing
import com.tracklab400.app.ui.theme.statValue
import com.tracklab400.app.ui.theme.trackLabColors
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletionScreen(
    uiState: CompletionUiState,
    onAddRep: (Long) -> Unit,
    onUndoLastRep: () -> Unit,
    onSetRpe: (Int) -> Unit,
    onSetSleepHours: (Double) -> Unit,
    onSetEnergy: (Int) -> Unit,
    onSetLegFeeling: (Int) -> Unit,
    onSetPain: (String, Int, String) -> Unit,
    onClearPain: () -> Unit,
    onShowPainWarning: () -> Unit,
    onDismissPainWarning: () -> Unit,
    onSetNotes: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var repSecondsText by rememberSaveable { mutableStateOf("") }
    var repInputError by rememberSaveable { mutableStateOf(false) }

    var showPainDialog by rememberSaveable { mutableStateOf(false) }
    var painLocation by rememberSaveable { mutableStateOf("") }
    var painSeverity by rememberSaveable { mutableIntStateOf(5) }
    var painDescription by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(uiState.sessionTitle) },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = TrackLabSpacing.md),
        ) {
            // Header: total time + rep counter + target
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(TrackLabSpacing.sm))
                Text(
                    text = stringResource(R.string.comp_total),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = TimeUtils.formatClock(uiState.totalTimeMs),
                    style = MaterialTheme.typography.statValue.copy(
                        fontSize = 48.sp,
                        lineHeight = 56.sp,
                        fontFamily = FontFamily.Monospace,
                    ),
                    color = Lime400,
                )
                Text(
                    text = stringResource(
                        R.string.comp_rep_counter,
                        uiState.completedReps,
                        uiState.expectedReps,
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (uiState.distanceM > 0) {
                    Text(
                        text = stringResource(
                            R.string.comp_distance,
                            "${uiState.distanceM} m · hedef ${targetText(uiState)}",
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(TrackLabSpacing.md))

            // Rep entry
            TrackLabSectionHeader(title = stringResource(R.string.comp_reps_list))
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = repSecondsText,
                    onValueChange = {
                        repSecondsText = it
                        repInputError = false
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.comp_correct_hint)) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                    ),
                    isError = repInputError,
                    singleLine = true,
                )
                TrackLabAccentButton(
                    text = stringResource(R.string.comp_lap),
                    onClick = {
                        val seconds = TimeUtils.parseSeconds(repSecondsText)
                        if (seconds != null && seconds > 0) {
                            onAddRep((seconds * 1000.0).roundToLong())
                            repSecondsText = ""
                        } else {
                            repInputError = true
                        }
                    },
                )
                if (uiState.reps.isNotEmpty()) {
                    OutlinedButton(onClick = onUndoLastRep) {
                        Text(stringResource(R.string.comp_reset))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (uiState.reps.isEmpty()) {
                Text(
                    text = stringResource(R.string.comp_empty_reps),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                uiState.reps.forEach { rep -> RepRow(rep) }
            }

            Spacer(Modifier.height(TrackLabSpacing.md))

            // Pain warning banner
            if (uiState.showPainWarning) {
                TrackLabCard(
                    containerColor = MaterialTheme.trackLabColors.warning.copy(alpha = 0.15f),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.trackLabColors.warning,
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.comp_pain_warning_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = stringResource(R.string.comp_pain_warning_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = onDismissPainWarning) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.comp_dismiss),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(TrackLabSpacing.md))
            }

            // Post-workout form
            TrackLabSectionHeader(title = stringResource(R.string.comp_post_form))
            Spacer(Modifier.height(8.dp))

            TrackLabSectionHeader(title = stringResource(R.string.comp_rpe_title))
            TrackLabSlider(
                value = uiState.rpe?.toFloat() ?: 5f,
                onValueChange = { onSetRpe(it.roundToInt()) },
                min = 1f,
                max = 10f,
                steps = 9,
                label = { stringResource(R.string.comp_rpe_label, it.roundToInt()) },
            )

            TrackLabSectionHeader(title = stringResource(R.string.comp_sleep_title))
            TrackLabSlider(
                value = uiState.sleepHours?.toFloat() ?: 8f,
                onValueChange = { onSetSleepHours(it.toDouble()) },
                min = 0f,
                max = 12f,
                steps = 24,
                label = { stringResource(R.string.comp_sleep_label, it) },
            )

            TrackLabSectionHeader(title = stringResource(R.string.comp_energy_title))
            TrackLabSlider(
                value = uiState.energyLevel?.toFloat() ?: 3f,
                onValueChange = { onSetEnergy(it.roundToInt()) },
                min = 1f,
                max = 5f,
                steps = 4,
                label = { stringResource(R.string.comp_energy_label, it.roundToInt()) },
            )

            TrackLabSectionHeader(title = stringResource(R.string.comp_leg_title))
            TrackLabSlider(
                value = uiState.legFeeling?.toFloat() ?: 3f,
                onValueChange = { onSetLegFeeling(it.roundToInt()) },
                min = 1f,
                max = 5f,
                steps = 4,
                label = { stringResource(R.string.comp_leg_label, it.roundToInt()) },
            )

            // Pain
            TrackLabSectionHeader(title = stringResource(R.string.comp_pain_title))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        painLocation = uiState.painLocation
                        painSeverity = uiState.painSeverity
                        painDescription = uiState.painDescription
                        showPainDialog = true
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        if (uiState.hasPain) {
                            stringResource(R.string.comp_pain_edit)
                        } else {
                            stringResource(R.string.comp_pain_add)
                        },
                    )
                }
                if (uiState.hasPain) {
                    OutlinedButton(onClick = onClearPain) {
                        Text(stringResource(R.string.comp_pain_clear))
                    }
                }
            }
            if (uiState.hasPain) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrackLabTag(text = uiState.painLocation.ifBlank { "-" })
                    TrackLabTag(text = "Şiddet ${uiState.painSeverity}/10")
                }
            }

            // Notes
            Spacer(Modifier.height(TrackLabSpacing.md))
            TrackLabSectionHeader(title = stringResource(R.string.comp_notes_title))
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = onSetNotes,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.comp_notes_label)) },
                minLines = 3,
            )

            Spacer(Modifier.height(TrackLabSpacing.lg))

            TrackLabAccentButton(
                text = stringResource(R.string.comp_finish),
                onClick = onSave,
                loading = uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(TrackLabSpacing.lg))
        }
    }

    if (showPainDialog) {
        PainReportDialog(
            location = painLocation,
            severity = painSeverity,
            description = painDescription,
            onLocationChange = { painLocation = it },
            onSeverityChange = { painSeverity = it },
            onDescriptionChange = { painDescription = it },
            onConfirm = {
                onSetPain(painLocation.trim(), painSeverity, painDescription.trim())
                if (painSeverity >= 5) onShowPainWarning()
                showPainDialog = false
            },
            onDismiss = { showPainDialog = false },
        )
    }
}

@Composable
private fun RepRow(rep: RepRecord) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.comp_rep_item, rep.repIndex),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = TimeUtils.formatSeconds(rep.actualMs),
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
        )
        Spacer(Modifier.width(12.dp))
        rep.deviationMs?.let {
            Text(
                text = deviationText(it),
                style = MaterialTheme.typography.labelMedium,
                color = if (it <= 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
            )
        }
    }
}

@Composable
private fun PainReportDialog(
    location: String,
    severity: Int,
    description: String,
    onLocationChange: (String) -> Unit,
    onSeverityChange: (Int) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.comp_pain_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = { Text(stringResource(R.string.comp_pain_location_hint)) },
                    singleLine = true,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.comp_pain_warning_title),
                    style = MaterialTheme.typography.labelLarge,
                )
                TrackLabSlider(
                    value = severity.toFloat(),
                    onValueChange = { onSeverityChange(it.roundToInt()) },
                    min = 1f,
                    max = 10f,
                    steps = 9,
                    label = { stringResource(R.string.comp_rpe_label, it.roundToInt()) },
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text(stringResource(R.string.comp_notes_label)) },
                    minLines = 2,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

private fun targetText(state: CompletionUiState): String {
    val min = state.targetMinMs
    val max = state.targetMaxMs
    return if (min != null && max != null) {
        "${TimeUtils.formatSeconds(min)}–${TimeUtils.formatSeconds(max)} sn"
    } else {
        "-"
    }
}

private fun deviationText(deviationMs: Long): String {
    val sign = if (deviationMs > 0) "+" else "−"
    return "$sign${TimeUtils.formatSeconds(kotlin.math.abs(deviationMs))} sn"
}

@Preview(showBackground = true)
@Composable
private fun CompletionScreenPreview() {
    MaterialTheme {
        CompletionScreen(
            uiState = CompletionUiState(
                sessionTitle = "Tempo 4×300m",
                expectedReps = 4,
                distanceM = 300,
                targetMinMs = 45_000,
                targetMaxMs = 48_000,
            ),
            onAddRep = {},
            onUndoLastRep = {},
            onSetRpe = {},
            onSetSleepHours = {},
            onSetEnergy = {},
            onSetLegFeeling = {},
            onSetPain = { _, _, _ -> },
            onClearPain = {},
            onShowPainWarning = {},
            onDismissPainWarning = {},
            onSetNotes = {},
            onSave = {},
            onBack = {},
        )
    }
}