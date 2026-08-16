package com.smartcity.greenpassport.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.LevelProgression
import com.smartcity.greenpassport.feature.profile.domain.GetExperienceUseCase
import com.smartcity.greenpassport.feature.profile.domain.GetPointsBalanceUseCase
import com.smartcity.greenpassport.feature.profile.domain.ObserveNotificationsEnabledUseCase
import com.smartcity.greenpassport.feature.profile.domain.ObserveProfileSessionUseCase
import com.smartcity.greenpassport.feature.profile.domain.SetNotificationsEnabledUseCase
import com.smartcity.greenpassport.feature.profile.domain.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeSession: ObserveProfileSessionUseCase,
    private val getPointsBalance: GetPointsBalanceUseCase,
    private val getExperience: GetExperienceUseCase,
    private val signOutUseCase: SignOutUseCase,
    observeNotificationsEnabled: ObserveNotificationsEnabledUseCase,
    private val setNotificationsEnabled: SetNotificationsEnabledUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                if (session == null) {
                    _uiState.update { it.copy(isLoading = false, userId = null) }
                    return@collectLatest
                }
                _uiState.update {
                    it.copy(isLoading = true, userId = session.userId, isAnonymous = session.isAnonymous)
                }
                val balance = getPointsBalance(session.userId)
                val experience = getExperience(session.userId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        points = balance.availablePoints,
                        level = LevelProgression.levelFor(experience),
                    )
                }
            }
        }

        viewModelScope.launch {
            observeNotificationsEnabled().collectLatest { enabled ->
                _uiState.update { it.copy(notificationsEnabled = enabled) }
            }
        }
    }

    fun onNotificationsToggle(enabled: Boolean) {
        viewModelScope.launch { setNotificationsEnabled(enabled) }
    }

    fun onSignOut() {
        viewModelScope.launch { signOutUseCase() }
    }
}
