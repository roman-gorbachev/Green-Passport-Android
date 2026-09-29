package com.smartcity.greenpassport.feature.auth.presentation.profilesetup.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.core.model.profile.AvatarStyle
import com.smartcity.greenpassport.core.model.profile.UserProfile
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.feature.auth.domain.IsTextAllowedUseCase
import com.smartcity.greenpassport.feature.auth.domain.ObserveAuthSessionUseCase
import com.smartcity.greenpassport.feature.auth.domain.ObserveUserProfileUseCase
import com.smartcity.greenpassport.feature.auth.domain.SaveUserProfileUseCase
import com.smartcity.greenpassport.feature.auth.domain.SignOutUseCase
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state.NameError
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state.ProfileSetupStep
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state.ProfileSetupUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    observeSession: ObserveAuthSessionUseCase,
    private val observeUserProfile: ObserveUserProfileUseCase,
    private val saveUserProfile: SaveUserProfileUseCase,
    private val isTextAllowed: IsTextAllowedUseCase,
    private val signOut: SignOutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState = observeSession()
        .filterNotNull()
        .distinctUntilChanged { old, new -> old.userId == new.userId }
        .flatMapLatest { session ->
            flow {
                prefill(session)
                emitAll(_uiState)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ProfileSetupUiState())

    fun onFirstNameChange(value: String) {
        _uiState.update { it.copy(firstName = value, firstNameError = null) }
    }

    fun onLastNameChange(value: String) {
        _uiState.update { it.copy(lastName = value, lastNameError = null) }
    }

    fun onCitySelected(city: String) {
        _uiState.update { it.copy(city = city, isCityMissing = false) }
    }

    fun onInterestToggled(category: TaskCategory) {
        _uiState.update { state ->
            val interests = if (category in state.interests) state.interests - category else state.interests + category
            state.copy(interests = interests, isInterestsMissing = false)
        }
    }

    fun onAvatarSelected(avatar: AvatarStyle) {
        _uiState.update { it.copy(avatar = avatar) }
    }

    fun onBack() {
        _uiState.update { state ->
            val previous = ProfileSetupStep.entries.getOrNull(state.step.ordinal - 1) ?: state.step
            state.copy(step = previous)
        }
    }

    fun onNext() {
        val state = _uiState.value
        if (state.isSaving || !isStepValid(state)) return
        val next = ProfileSetupStep.entries.getOrNull(state.step.ordinal + 1)
        if (next != null) {
            _uiState.update { it.copy(step = next) }
        } else {
            save(state)
        }
    }

    fun onSignOut() {
        viewModelScope.launch { runCatching { signOut() } }
    }

    private fun isStepValid(state: ProfileSetupUiState): Boolean = when (state.step) {
        ProfileSetupStep.NAME -> {
            val firstNameError = validateName(state.firstName)
            val lastNameError = validateName(state.lastName)
            _uiState.update { it.copy(firstNameError = firstNameError, lastNameError = lastNameError) }
            firstNameError == null && lastNameError == null
        }

        ProfileSetupStep.CITY -> {
            _uiState.update { it.copy(isCityMissing = state.city == null) }
            state.city != null
        }

        ProfileSetupStep.INTERESTS -> {
            _uiState.update { it.copy(isInterestsMissing = state.interests.isEmpty()) }
            state.interests.isNotEmpty()
        }

        ProfileSetupStep.AVATAR -> true
    }

    private fun validateName(name: String): NameError? {
        val trimmed = name.trim()
        return when {
            trimmed.length !in MIN_NAME_LENGTH..MAX_NAME_LENGTH -> NameError.LENGTH
            !NAME_PATTERN.matches(trimmed) -> NameError.CHARACTERS
            !isTextAllowed(trimmed) -> NameError.INAPPROPRIATE
            else -> null
        }
    }

    private fun save(state: ProfileSetupUiState) {
        val userId = state.userId ?: return
        val city = state.city ?: return
        val profile = UserProfile(
            userId = userId,
            firstName = state.firstName.trim(),
            lastName = state.lastName.trim(),
            city = city,
            interests = state.interests,
            avatar = state.avatar,
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, hasSaveError = false) }
            runCatching { saveUserProfile(profile) }
                .onSuccess { _uiState.update { it.copy(isSaving = false, isSaved = true) } }
                .onFailure { error ->
                    Log.e(TAG, "Failed to save profile", error)
                    _uiState.update {
                        if (error is ContentRejectedException) {
                            it.copy(
                                isSaving = false,
                                step = ProfileSetupStep.NAME,
                                firstNameError = NameError.INAPPROPRIATE,
                            )
                        } else {
                            it.copy(isSaving = false, hasSaveError = true)
                        }
                    }
                }
        }
    }

    private suspend fun prefill(session: AuthSession) {
        if (_uiState.value.isPrefilled && _uiState.value.userId == session.userId) return
        _uiState.value = ProfileSetupUiState()
        val profile = runCatching { observeUserProfile(session.userId).first() }.getOrNull()
        val suggestedNames = session.displayName.orEmpty().trim().split(NAME_SEPARATOR, limit = 2)
        _uiState.update { state ->
            if (profile != null) {
                state.copy(
                    isPrefilled = true,
                    userId = session.userId,
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    city = profile.city.takeIf { it.isNotBlank() },
                    interests = profile.interests,
                    avatar = profile.avatar,
                )
            } else {
                state.copy(
                    isPrefilled = true,
                    userId = session.userId,
                    firstName = state.firstName.ifEmpty { suggestedNames.getOrNull(0).orEmpty() },
                    lastName = state.lastName.ifEmpty { suggestedNames.getOrNull(1).orEmpty() },
                )
            }
        }
    }

    companion object {
        private const val TAG = "ProfileSetupViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
        private const val MIN_NAME_LENGTH = 2
        private const val MAX_NAME_LENGTH = 30
        private val NAME_PATTERN = Regex("^[\\p{L}][\\p{L} \\-]*$")
        private val NAME_SEPARATOR = Regex("\\s+")
    }
}
