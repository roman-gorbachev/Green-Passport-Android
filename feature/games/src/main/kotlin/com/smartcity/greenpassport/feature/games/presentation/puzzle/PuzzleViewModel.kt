package com.smartcity.greenpassport.feature.games.presentation.puzzle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.games.domain.GameId
import com.smartcity.greenpassport.feature.games.domain.ObserveGamesSessionUseCase
import com.smartcity.greenpassport.feature.games.domain.SubmitGameResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAIR_COUNT = 6
private const val OPTIMAL_MOVES = PAIR_COUNT
private const val BASE_SCORE = 100
private const val PENALTY_PER_EXTRA_MOVE = 5
private const val MINIMUM_SCORE = 10
private const val MISMATCH_DELAY_MILLIS = 800L

@HiltViewModel
class PuzzleViewModel @Inject constructor(
    private val submitGameResult: SubmitGameResultUseCase,
    private val observeSession: ObserveGamesSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PuzzleUiState(cards = newShuffledDeck()))
    val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

    private var pendingFirstCardId: Int? = null

    fun onCardClick(cardId: Int) {
        val state = _uiState.value
        if (state.isInputLocked) return
        val card = state.cards.firstOrNull { it.id == cardId } ?: return
        if (card.isFaceUp || card.isMatched) return

        val firstId = pendingFirstCardId
        if (firstId == null) {
            pendingFirstCardId = cardId
            _uiState.update { it.copy(cards = it.cards.flip(cardId)) }
            return
        }

        _uiState.update { it.copy(cards = it.cards.flip(cardId), moves = it.moves + 1, isInputLocked = true) }
        pendingFirstCardId = null

        val firstCard = state.cards.first { it.id == firstId }
        val isMatch = firstCard.iconIndex == card.iconIndex

        viewModelScope.launch {
            if (!isMatch) delay(MISMATCH_DELAY_MILLIS)
            _uiState.update { current ->
                val updatedCards = current.cards.map { existing ->
                    when {
                        existing.id != firstId && existing.id != cardId -> existing
                        isMatch -> existing.copy(isMatched = true)
                        else -> existing.copy(isFaceUp = false)
                    }
                }
                current.copy(cards = updatedCards, isInputLocked = false)
            }
            maybeFinish()
        }
    }

    private suspend fun maybeFinish() {
        val state = _uiState.value
        if (state.isFinished || !state.cards.all { it.isMatched }) return

        val extraMoves = (state.moves - OPTIMAL_MOVES).coerceAtLeast(0)
        val score = (BASE_SCORE - extraMoves * PENALTY_PER_EXTRA_MOVE).coerceAtLeast(MINIMUM_SCORE)
        _uiState.update { it.copy(isFinished = true, score = score) }

        val userId = observeSession().first()?.userId ?: return
        try {
            submitGameResult(userId, GameId.ECO_PUZZLE, score)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Unit
        }
    }

    fun onRestart() {
        pendingFirstCardId = null
        _uiState.value = PuzzleUiState(cards = newShuffledDeck())
    }
}

private fun List<PuzzleCard>.flip(cardId: Int): List<PuzzleCard> =
    map { if (it.id == cardId) it.copy(isFaceUp = true) else it }

private fun newShuffledDeck(): List<PuzzleCard> =
    (0 until PAIR_COUNT).flatMap { iconIndex -> listOf(iconIndex, iconIndex) }
        .shuffled()
        .mapIndexed { id, iconIndex -> PuzzleCard(id = id, iconIndex = iconIndex) }
