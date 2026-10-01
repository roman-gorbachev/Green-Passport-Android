package com.smartcity.greenpassport.feature.games.presentation.web.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.games.Game
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.core.model.rewards.RewardFailureException
import com.smartcity.greenpassport.feature.games.domain.ObserveGamesUseCase
import com.smartcity.greenpassport.feature.games.domain.SubmitGameResultUseCase
import com.smartcity.greenpassport.feature.games.presentation.web.state.GameWebUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameWebViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeGames: ObserveGamesUseCase,
    private val submitGameResult: SubmitGameResultUseCase,
) : ViewModel() {

    private val gameId: String = checkNotNull(savedStateHandle["gameId"])

    private val rewardState = MutableStateFlow(GameWebUiState())

    val uiState = observeGameWebUiState(observeGames()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        GameWebUiState(),
    )

    fun onFinish(score: Int) {
        viewModelScope.launch {
            runCatching { submitGameResult(gameId, score) }
                .onSuccess { reward ->
                    if (reward != null && (reward.points > 0 || reward.streakBonus > 0)) {
                        rewardState.update {
                            it.copy(lastReward = reward, rewardFailure = null, rewardCount = it.rewardCount + 1)
                        }
                    }
                }
                .onFailure { error ->
                    Log.w(TAG, "Failed to record game result", error)
                    val failure = (error as? RewardFailureException)?.failure ?: RewardFailure.UNKNOWN
                    rewardState.update {
                        it.copy(lastReward = null, rewardFailure = failure, rewardCount = it.rewardCount + 1)
                    }
                }
        }
    }

    private fun observeGameWebUiState(games: Flow<List<Game>>): Flow<GameWebUiState> {
        val game = games
            .map { list -> list.firstOrNull { it.id == gameId } }
            .catch { emit(null) }
        return combine(game, rewardState) { currentGame, reward ->
            reward.copy(game = currentGame, isGameMissing = currentGame == null)
        }
    }

    companion object {
        private const val TAG = "GameWebViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
