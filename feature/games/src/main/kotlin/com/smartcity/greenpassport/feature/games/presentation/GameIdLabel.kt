package com.smartcity.greenpassport.feature.games.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.feature.games.R
import com.smartcity.greenpassport.feature.games.domain.GameId

fun gameTitleRes(gameId: GameId): Int = when (gameId) {
    GameId.ECO_PUZZLE -> R.string.game_eco_puzzle_title
    GameId.WASTE_SORTING -> R.string.game_waste_sorting_title
    GameId.ECO_MAZE -> R.string.game_eco_maze_title
    GameId.ECO_QUIZ -> R.string.game_eco_quiz_title
}

fun gameIcon(gameId: GameId): ImageVector = when (gameId) {
    GameId.ECO_PUZZLE -> Icons.Filled.Extension
    GameId.WASTE_SORTING -> Icons.Filled.DeleteSweep
    GameId.ECO_MAZE -> Icons.Filled.Explore
    GameId.ECO_QUIZ -> Icons.Filled.Quiz
}
