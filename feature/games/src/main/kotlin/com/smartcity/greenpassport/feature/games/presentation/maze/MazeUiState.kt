package com.smartcity.greenpassport.feature.games.presentation.maze

enum class MazeDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT,
}

data class MazeUiState(
    val grid: List<List<MazeCellType>> = parseMazeLayout(),
    val playerPosition: MazePosition = findStartPosition(parseMazeLayout()),
    val collectedItems: Set<MazePosition> = emptySet(),
    val score: Int = 0,
    val isFinished: Boolean = false,
)
