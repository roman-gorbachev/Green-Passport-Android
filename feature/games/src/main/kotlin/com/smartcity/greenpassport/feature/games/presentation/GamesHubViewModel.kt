package com.smartcity.greenpassport.feature.games.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.games.domain.ObserveGameProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesHubViewModel @Inject constructor(
    observeGameProgress: ObserveGameProgressUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamesHubUiState())
    val uiState: StateFlow<GamesHubUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeGameProgress().collectLatest { progress ->
                val bestScores = progress.associate { it.gameId to it.bestScore }
                _uiState.update { it.copy(bestScores = bestScores, isLoading = false) }
            }
        }
    }
}
