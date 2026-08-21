package com.smartcity.greenpassport.feature.games.presentation.maze

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.games.domain.GameId
import com.smartcity.greenpassport.feature.games.domain.ObserveGamesSessionUseCase
import com.smartcity.greenpassport.feature.games.domain.SubmitGameResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val POINTS_PER_ITEM = 20

@HiltViewModel
class MazeViewModel @Inject constructor(
    private val submitGameResult: SubmitGameResultUseCase,
    private val observeSession: ObserveGamesSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MazeUiState())
    val uiState: StateFlow<MazeUiState> = _uiState.asStateFlow()

    fun onMove(direction: MazeDirection) {
        val state = _uiState.value
        if (state.isFinished) return

        val target = state.playerPosition.moved(direction)
        val targetCell = state.grid.cellAt(target) ?: return
        if (targetCell == MazeCellType.WALL) return

        val collected = if (targetCell == MazeCellType.ITEM && target !in state.collectedItems) {
            state.collectedItems + target
        } else {
            state.collectedItems
        }

        val isFinished = targetCell == MazeCellType.EXIT
        val score = collected.size * POINTS_PER_ITEM

        _uiState.update {
            it.copy(
                playerPosition = target,
                collectedItems = collected,
                score = score,
                isFinished = isFinished,
            )
        }

        if (isFinished) {
            viewModelScope.launch {
                val userId = observeSession().first()?.userId ?: return@launch
                runCatching {
                    submitGameResult(userId, GameId.ECO_MAZE, score)
                }.onFailure { error ->
                    Log.e("MazeViewModel::onMove()", error.message.orEmpty())
                }
            }
        }
    }

    fun onRestart() {
        _uiState.value = MazeUiState()
    }
}

private fun MazePosition.moved(direction: MazeDirection): MazePosition = when (direction) {
    MazeDirection.UP -> copy(row = row - 1)
    MazeDirection.DOWN -> copy(row = row + 1)
    MazeDirection.LEFT -> copy(col = col - 1)
    MazeDirection.RIGHT -> copy(col = col + 1)
}

private fun List<List<MazeCellType>>.cellAt(position: MazePosition): MazeCellType? =
    getOrNull(position.row)?.getOrNull(position.col)
