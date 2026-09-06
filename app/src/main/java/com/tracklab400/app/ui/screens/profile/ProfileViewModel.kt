package com.tracklab400.app.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.TrackLabApplication
import com.tracklab400.app.data.model.Equipment
import com.tracklab400.app.data.model.GymStatus
import com.tracklab400.app.data.model.ProfileField
import com.tracklab400.app.data.model.ProfileValidator
import com.tracklab400.app.data.model.StopwatchType
import com.tracklab400.app.data.model.TrackAccess
import com.tracklab400.app.data.model.TrainingExperience
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ProfileEvent {
    data object ProfileSaved : ProfileEvent
    data object DemoApplied : ProfileEvent
    data object ProfileDeleted : ProfileEvent
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as TrackLabApplication).container.profileRepository

    val onboardingCompleted: Flow<Boolean?> = repository.onboardingCompleted
    val isDemoMode: Flow<Boolean> = repository.isDemoMode

    private val _formState = MutableStateFlow(ProfileFormState())
    val formState: StateFlow<ProfileFormState> = _formState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    private var existingIsDemo = false
    private var loadedForEdit = false

    init {
        viewModelScope.launch {
            repository.profile.collect { profile ->
                if (profile != null && !loadedForEdit) {
                    loadedForEdit = true
                    existingIsDemo = profile.isDemo
                    _formState.value = ProfileFormState.from(profile)
                }
            }
        }
    }

    fun save() {
        val state = _formState.value
        val errors = ProfileValidator.validate(state)
        if (errors.isNotEmpty()) {
            _formState.update { it.copy(errors = errors) }
            return
        }
        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }
            val profile = state.toProfile(isDemo = existingIsDemo)
            repository.saveProfile(profile)
            _formState.update { it.copy(isSaving = false) }
            _events.emit(ProfileEvent.ProfileSaved)
        }
    }

    fun applyDemo() {
        viewModelScope.launch {
            repository.applyDemoProfile()
            _events.emit(ProfileEvent.DemoApplied)
        }
    }

    fun deleteProfile() {
        viewModelScope.launch {
            repository.clearProfile()
            _events.emit(ProfileEvent.ProfileDeleted)
        }
    }

    fun updateNickname(value: String) = updateField(ProfileField.NICKNAME) { it.copy(nickname = value) }
    fun updateAge(value: String) = updateField(ProfileField.AGE) { it.copy(age = value) }
    fun updateCurrent100(value: String) = updateField(ProfileField.CURRENT_100) { it.copy(current100 = value) }
    fun updateCurrent200(value: String) = updateField(ProfileField.CURRENT_200) { it.copy(current200 = value) }
    fun updateCurrent300(value: String) = updateField(ProfileField.CURRENT_300) { it.copy(current300 = value) }
    fun updateCurrent400(value: String) = updateField(ProfileField.CURRENT_400) { it.copy(current400 = value) }
    fun updateInjury(value: String) = updateField(ProfileField.INJURY) { it.copy(injuryInfo = value) }

    fun selectTargetDistance(distance: Int) =
        updateField(ProfileField.TARGET_TIME) { it.copy(targetDistance = distance) }

    fun updateTargetTime(value: String) =
        updateField(ProfileField.TARGET_TIME) { it.copy(targetTime = value) }

    fun selectPrepWeeks(weeks: Int) =
        updateField(ProfileField.PREP_WEEKS) { it.copy(prepWeeks = weeks) }

    fun selectTrainingDays(days: Int) =
        updateField(ProfileField.TRAINING_DAYS) { it.copy(trainingDaysPerWeek = days) }

    fun selectStartDate(millis: Long) =
        updateField(ProfileField.START_DATE) { it.copy(startDateMillis = millis) }

    fun selectTrackAccess(value: TrackAccess) =
        updateField(ProfileField.TRACK_ACCESS) { it.copy(trackAccess = value) }

    fun selectGymStatus(value: GymStatus) =
        updateField(ProfileField.GYM_STATUS) { it.copy(gymStatus = value) }

    fun selectExperience(value: TrainingExperience) =
        updateField(ProfileField.EXPERIENCE) { it.copy(experience = value) }

    fun selectStopwatchType(value: StopwatchType) =
        updateField(ProfileField.STOPWATCH_TYPE) { it.copy(stopwatchType = value) }

    fun toggleEquipment(value: Equipment) =
        updateField(ProfileField.EQUIPMENT) { state ->
            state.copy(
                equipment = if (value in state.equipment) {
                    state.equipment - value
                } else {
                    state.equipment + value
                },
            )
        }

    private fun updateField(
        field: ProfileField,
        transform: (ProfileFormState) -> ProfileFormState,
    ) {
        _formState.update { state ->
            transform(state).copy(errors = state.errors - field)
        }
    }
}
