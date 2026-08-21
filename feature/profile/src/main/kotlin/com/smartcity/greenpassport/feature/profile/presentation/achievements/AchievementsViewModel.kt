package com.smartcity.greenpassport.feature.profile.presentation.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.profile.domain.GetAchievementsUseCase
import com.smartcity.greenpassport.feature.profile.domain.ObserveProfileSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.copy

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val getAchievements: GetAchievementsUseCase,
    observeSession: ObserveProfileSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                currentUserId = session?.userId
                if (session == null) {
                    _uiState.update { it.copy(achievements = emptyList(), isLoading = false) }
                    return@collectLatest
                }
                loadAchievements(session.userId)
            }
        }
    }

    fun retry() {
        val userId = currentUserId ?: return
        viewModelScope.launch { loadAchievements(userId) }
    }

    private suspend fun loadAchievements(userId: String) {
        _uiState.update { it.copy(isLoading = true, hasError = false) }
        try {
            val achievements = getAchievements(userId)
            _uiState.update { it.copy(achievements = achievements, isLoading = false) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            _uiState.update { it.copy(isLoading = false, hasError = true) }
        }
    }
}
