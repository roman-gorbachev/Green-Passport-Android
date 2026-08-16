package com.smartcity.greenpassport.feature.games.presentation.puzzle

data class PuzzleCard(
    val id: Int,
    val iconIndex: Int,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false,
)

data class PuzzleUiState(
    val cards: List<PuzzleCard> = emptyList(),
    val moves: Int = 0,
    val score: Int = 0,
    val isFinished: Boolean = false,
    val isInputLocked: Boolean = false,
)
