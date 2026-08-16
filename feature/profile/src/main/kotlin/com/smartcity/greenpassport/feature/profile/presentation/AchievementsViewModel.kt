package com.smartcity.greenpassport.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.profile.domain.GetAchievementsUseCase
import com.smartcity.greenpassport.feature.profile.domain.ObserveProfileSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val getAchievements: GetAchievementsUseCase,
    observeSession: ObserveProfileSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                if (session == null) {
                    _uiState.update { it.copy(achievements = emptyList(), isLoading = false) }
                    return@collectLatest
                }
                _uiState.update { it.copy(isLoading = true) }
                val achievements = getAchievements(session.userId)
                _uiState.update { it.copy(achievements = achievements, isLoading = false) }
            }
        }
    }
}
