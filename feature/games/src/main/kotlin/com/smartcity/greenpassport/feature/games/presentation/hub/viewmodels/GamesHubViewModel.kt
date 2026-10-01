package com.smartcity.greenpassport.feature.games.presentation.hub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.games.domain.ObserveGameProgressUseCase
import com.smartcity.greenpassport.feature.games.domain.ObserveGamesUseCase
import com.smartcity.greenpassport.feature.games.presentation.hub.state.GamesHubUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GamesHubViewModel @Inject constructor(
    private val observeGames: ObserveGamesUseCase,
    private val observeGameProgress: ObserveGameProgressUseCase,
) : ViewModel() {

    private val retryRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val uiState = observeGamesHubUiState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        GamesHubUiState(),
    )

    fun retry() {
        retryRequests.tryEmit(Unit)
    }

    private fun observeGamesHubUiState(): Flow<GamesHubUiState> {
        return retryRequests
            .onStart { emit(Unit) }
            .flatMapLatest {
                combine(observeGames(), observeGameProgress().catch { emit(emptyList()) }) { games, progress ->
                    GamesHubUiState(
                        games = games,
                        bestScores = progress.associate { it.gameId to it.bestScore },
                        isLoading = false,
                    )
                }
                    .onStart { emit(GamesHubUiState()) }
                    .catch { emit(GamesHubUiState(isLoading = false, hasError = true)) }
            }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
