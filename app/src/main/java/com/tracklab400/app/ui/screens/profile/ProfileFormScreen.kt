package com.tracklab400.app.ui.screens.profile

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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.tracklab400.app.R
import com.tracklab400.app.data.model.Equipment
import com.tracklab400.app.data.model.GymStatus
import com.tracklab400.app.data.model.ProfileField
import com.tracklab400.app.data.model.ProfileFieldError
import com.tracklab400.app.data.model.ProfileValidator
import com.tracklab400.app.data.model.StopwatchType
import com.tracklab400.app.data.model.TrackAccess
import com.tracklab400.app.data.model.TrainingExperience
import com.tracklab400.app.ui.components.TrackLabButton
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabChoiceChips
import com.tracklab400.app.ui.components.TrackLabSectionHeader
import com.tracklab400.app.ui.components.TrackLabTextField
import com.tracklab400.app.ui.theme.TrackLabSpacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.flow.Flow

data class ProfileFormCallbacks(
    val onNicknameChange: (String) -> Unit,
    val onAgeChange: (String) -> Unit,
    val onCurrent100Change: (String) -> Unit,
    val onCurrent200Change: (String) -> Unit,
    val onCurrent300Change: (String) -> Unit,
    val onCurrent400Change: (String) -> Unit,
    val onTargetDistanceChange: (Int) -> Unit,
    val onTargetTimeChange: (String) -> Unit,
    val onPrepWeeksChange: (Int) -> Unit,
    val onTrainingDaysChange: (Int) -> Unit,
    val onStartDateChange: (Long) -> Unit,
    val onTrackAccessChange: (TrackAccess) -> Unit,
    val onGymStatusChange: (GymStatus) -> Unit,
    val onEquipmentToggle: (Equipment) -> Unit,
    val onExperienceChange: (TrainingExperience) -> Unit,
    val onInjuryChange: (String) -> Unit,
    val onStopwatchTypeChange: (StopwatchType) -> Unit,
    val onSave: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFormScreen(
    formState: ProfileFormState,
    events: Flow<ProfileEvent>,
    callbacks: ProfileFormCallbacks,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        events.collect { event ->
            if (event == ProfileEvent.ProfileSaved) onDone()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.form_title_create)) })
        },
    ) { innerPadding ->
        ProfileFormContent(
            formState = formState,
            callbacks = callbacks,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
fun ProfileFormContent(
    formState: ProfileFormState,
    callbacks: ProfileFormCallbacks,
    modifier: Modifier = Modifier,
    footer: @Composable () -> Unit = {},
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(TrackLabSpacing.md),
    ) {
        TrackLabSectionHeader(title = stringResource(R.string.section_personal))
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabCard {
            TrackLabTextField(
                value = formState.nickname,
                onValueChange = callbacks.onNicknameChange,
                label = stringResource(R.string.label_nickname),
                keyboardType = KeyboardType.Text,
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TrackLabTextField(
                value = formState.age,
                onValueChange = callbacks.onAgeChange,
                label = stringResource(R.string.label_age),
                keyboardType = KeyboardType.Number,
                isError = formState.errors.containsKey(ProfileField.AGE),
                errorText = choiceErrorText(formState.errors[ProfileField.AGE]) {
                    when (formState.errors[ProfileField.AGE]) {
                        ProfileFieldError.INVALID -> stringResource(R.string.error_invalid_number)
                        else -> stringResource(R.string.error_age_range)
                    }
                },
            )
        }

        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabSectionHeader(title = stringResource(R.string.section_times))
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabCard {
            TimeField(
                value = formState.current100,
                onChange = callbacks.onCurrent100Change,
                label = stringResource(R.string.label_current_100),
                field = ProfileField.CURRENT_100,
                distance = 100,
                errors = formState.errors,
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TimeField(
                value = formState.current200,
                onChange = callbacks.onCurrent200Change,
                label = stringResource(R.string.label_current_200),
                field = ProfileField.CURRENT_200,
                distance = 200,
                errors = formState.errors,
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TimeField(
                value = formState.current300,
                onChange = callbacks.onCurrent300Change,
                label = stringResource(R.string.label_current_300),
                field = ProfileField.CURRENT_300,
                distance = 300,
                errors = formState.errors,
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TimeField(
                value = formState.current400,
                onChange = callbacks.onCurrent400Change,
                label = stringResource(R.string.label_current_400),
                field = ProfileField.CURRENT_400,
                distance = 400,
                errors = formState.errors,
            )
            Spacer(Modifier.height(TrackLabSpacing.md))
            Text(
                text = stringResource(R.string.label_target_distance),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            TrackLabChoiceChips(
                options = listOf(100, 200, 300, 400),
                optionLabel = { distance -> stringResource(R.string.distance_format, distance) },
                isSelected = { it == formState.targetDistance },
                onToggle = callbacks.onTargetDistanceChange,
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TimeField(
                value = formState.targetTime,
                onChange = callbacks.onTargetTimeChange,
                label = stringResource(R.string.label_target_time),
                field = ProfileField.TARGET_TIME,
                distance = formState.targetDistance,
                errors = formState.errors,
            )
        }

        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabSectionHeader(title = stringResource(R.string.section_plan))
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabCard {
            Text(
                text = stringResource(R.string.label_prep_weeks),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            TrackLabChoiceChips(
                options = listOf(4, 6, 8, 10, 12, 16),
                optionLabel = { weeks -> stringResource(R.string.week_format, weeks) },
                isSelected = { it == formState.prepWeeks },
                onToggle = callbacks.onPrepWeeksChange,
            )
            Spacer(Modifier.height(TrackLabSpacing.md))
            Text(
                text = stringResource(R.string.label_training_days),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            TrackLabChoiceChips(
                options = listOf(3, 4, 5, 6),
                optionLabel = { days -> stringResource(R.string.day_format, days) },
                isSelected = { it == formState.trainingDaysPerWeek },
                onToggle = callbacks.onTrainingDaysChange,
            )
            Spacer(Modifier.height(TrackLabSpacing.md))
            TrackLabCard(
                onClick = { showDatePicker = true },
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(TrackLabSpacing.sm))
                    Column {
                        Text(
                            text = stringResource(R.string.label_start_date),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = formatStartDate(formState.startDateMillis),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabSectionHeader(title = stringResource(R.string.section_environment))
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabCard {
            Text(
                text = stringResource(R.string.label_track_access),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            TrackLabChoiceChips(
                options = TrackAccess.entries,
                optionLabel = { access -> stringResource(access.labelRes()) },
                isSelected = { it == formState.trackAccess },
                onToggle = callbacks.onTrackAccessChange,
            )
            ChoiceErrorLine(formState.errors.containsKey(ProfileField.TRACK_ACCESS))

            Spacer(Modifier.height(TrackLabSpacing.md))
            Text(
                text = stringResource(R.string.label_gym),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            TrackLabChoiceChips(
                options = GymStatus.entries,
                optionLabel = { status -> stringResource(status.labelRes()) },
                isSelected = { it == formState.gymStatus },
                onToggle = callbacks.onGymStatusChange,
            )
            ChoiceErrorLine(formState.errors.containsKey(ProfileField.GYM_STATUS))

            Spacer(Modifier.height(TrackLabSpacing.md))
            Text(
                text = stringResource(R.string.label_equipment),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            TrackLabChoiceChips(
                options = Equipment.entries,
                optionLabel = { equipment -> stringResource(equipment.labelRes()) },
                isSelected = { it in formState.equipment },
                onToggle = callbacks.onEquipmentToggle,
            )
        }

        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabSectionHeader(title = stringResource(R.string.section_experience))
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabCard {
            Text(
                text = stringResource(R.string.label_experience),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            TrackLabChoiceChips(
                options = TrainingExperience.entries,
                optionLabel = { experience -> stringResource(experience.labelRes()) },
                isSelected = { it == formState.experience },
                onToggle = callbacks.onExperienceChange,
            )
            ChoiceErrorLine(formState.errors.containsKey(ProfileField.EXPERIENCE))

            Spacer(Modifier.height(TrackLabSpacing.md))
            TrackLabTextField(
                value = formState.injuryInfo,
                onValueChange = callbacks.onInjuryChange,
                label = stringResource(R.string.label_injury),
                keyboardType = KeyboardType.Text,
                singleLine = false,
                minLines = 2,
            )
        }

        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabSectionHeader(title = stringResource(R.string.section_measurement))
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabCard {
            Text(
                text = stringResource(R.string.label_stopwatch),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            TrackLabChoiceChips(
                options = StopwatchType.entries,
                optionLabel = { type -> stringResource(type.labelRes()) },
                isSelected = { it == formState.stopwatchType },
                onToggle = callbacks.onStopwatchTypeChange,
            )
            ChoiceErrorLine(formState.errors.containsKey(ProfileField.STOPWATCH_TYPE))
        }

        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabButton(
            text = stringResource(R.string.action_save_profile),
            onClick = callbacks.onSave,
            modifier = Modifier.fillMaxWidth(),
            loading = formState.isSaving,
        )
        footer()
        Spacer(Modifier.height(TrackLabSpacing.lg))
    }

    if (showDatePicker) {
        StartDatePickerDialog(
            currentMillis = formState.startDateMillis,
            onConfirm = callbacks.onStartDateChange,
            onDismiss = { showDatePicker = false },
        )
    }
}

@Composable
private fun TimeField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    field: ProfileField,
    distance: Int,
    errors: Map<ProfileField, ProfileFieldError>,
) {
    TrackLabTextField(
        value = value,
        onValueChange = onChange,
        label = label,
        keyboardType = KeyboardType.Decimal,
        isError = errors.containsKey(field),
        errorText = timeFieldErrorText(field, distance, errors),
    )
}

@Composable
private fun timeFieldErrorText(
    field: ProfileField,
    distance: Int,
    errors: Map<ProfileField, ProfileFieldError>,
): String? {
    val error = errors[field] ?: return null
    return when (error) {
        ProfileFieldError.EMPTY -> stringResource(R.string.error_required)
        ProfileFieldError.INVALID -> stringResource(R.string.error_invalid_time)
        ProfileFieldError.NON_POSITIVE -> stringResource(R.string.error_non_positive)
        ProfileFieldError.OUT_OF_RANGE -> {
            val range = ProfileValidator.rangeForDistance(distance)
            if (range != null) {
                stringResource(
                    R.string.error_out_of_range,
                    range.start.toInt(),
                    range.endInclusive.toInt(),
                )
            } else {
                null
            }
        }
        ProfileFieldError.NOT_BELOW_CURRENT ->
            stringResource(R.string.error_target_not_below, distance)
    }
}

@Composable
private fun ChoiceErrorLine(hasError: Boolean) {
    if (hasError) {
        Spacer(Modifier.height(TrackLabSpacing.xs))
        Text(
            text = stringResource(R.string.error_choice_required),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun choiceErrorText(
    error: ProfileFieldError?,
    message: @Composable () -> String,
): String? = if (error != null) message() else null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartDatePickerDialog(
    currentMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = currentMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let(onConfirm)
                    onDismiss()
                },
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    ) {
        DatePicker(state = state)
    }
}

private fun formatStartDate(millis: Long): String {
    val format = SimpleDateFormat("d MMMM yyyy", Locale("tr"))
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(Date(millis))
}

private fun TrackAccess.labelRes(): Int = when (this) {
    TrackAccess.PIST_VAR -> R.string.track_yes
    TrackAccess.PIST_YOK -> R.string.track_no
}

private fun GymStatus.labelRes(): Int = when (this) {
    GymStatus.SALON_VAR -> R.string.gym_yes
    GymStatus.SALON_YOK -> R.string.gym_no
}

private fun Equipment.labelRes(): Int = when (this) {
    Equipment.YOK -> R.string.equipment_none
    Equipment.VUCUT_AGIRLIGI -> R.string.equipment_bodyweight
    Equipment.DUMBBELL -> R.string.equipment_dumbbell
    Equipment.BARBELL -> R.string.equipment_barbell
    Equipment.TRAP_BAR -> R.string.equipment_trapbar
    Equipment.BANT -> R.string.equipment_band
}

private fun TrainingExperience.labelRes(): Int = when (this) {
    TrainingExperience.BASLANGIC -> R.string.experience_beginner
    TrainingExperience.ORTA -> R.string.experience_intermediate
    TrainingExperience.ILERI -> R.string.experience_advanced
}

private fun StopwatchType.labelRes(): Int = when (this) {
    StopwatchType.EL_KRONOMETRESI -> R.string.stopwatch_hand
    StopwatchType.ELEKTRONIK -> R.string.stopwatch_electronic
}
