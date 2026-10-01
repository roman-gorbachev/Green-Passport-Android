package com.smartcity.greenpassport.feature.games.presentation.hub.state

import com.smartcity.greenpassport.core.model.games.Game

data class GamesHubUiState(
    val games: List<Game> = emptyList(),
    val bestScores: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
