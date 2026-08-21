package com.smartcity.greenpassport.feature.games.presentation.sorting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.games.domain.GameId
import com.smartcity.greenpassport.feature.games.domain.ObserveGamesSessionUseCase
import com.smartcity.greenpassport.feature.games.domain.SubmitGameResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

private const val GAME_DURATION_SECONDS = 30
private const val POINTS_PER_CORRECT_ANSWER = 10
private const val TICK_INTERVAL_MILLIS = 1_000L

@HiltViewModel
class WasteSortingViewModel @Inject constructor(
    private val submitGameResult: SubmitGameResultUseCase,
    private val observeSession: ObserveGamesSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        WasteSortingUiState(currentItem = wasteItemPool.random(), secondsRemaining = GAME_DURATION_SECONDS),
    )
    val uiState: StateFlow<WasteSortingUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.secondsRemaining > 0) {
                delay(TICK_INTERVAL_MILLIS.milliseconds)
                _uiState.update { it.copy(secondsRemaining = it.secondsRemaining - 1) }
            }
            finishGame()
        }
    }

    fun onCategorySelected(category: WasteCategory) {
        val state = _uiState.value
        if (state.isFinished) return
        val current = state.currentItem ?: return

        val newScore = if (current.category == category) {
            state.score + POINTS_PER_CORRECT_ANSWER
        } else {
            state.score
        }

        _uiState.update { it.copy(score = newScore, currentItem = wasteItemPool.random()) }
    }

    private fun finishGame() {
        _uiState.update { it.copy(isFinished = true, currentItem = null) }
        viewModelScope.launch {
            val userId = observeSession().first()?.userId ?: return@launch
            runCatching {
                submitGameResult(userId, GameId.WASTE_SORTING, _uiState.value.score)
            }.onFailure { error ->
                Log.e("WasteSortingViewModel::finishGame()", error.message.orEmpty())
            }
        }
    }

    fun onRestart() {
        _uiState.value = WasteSortingUiState(
            currentItem = wasteItemPool.random(),
            secondsRemaining = GAME_DURATION_SECONDS,
        )
        startTimer()
    }
}
