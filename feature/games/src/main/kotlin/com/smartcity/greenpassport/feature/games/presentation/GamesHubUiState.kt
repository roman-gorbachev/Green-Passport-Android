package com.smartcity.greenpassport.feature.games.presentation

data class GamesHubUiState(
    val bestScores: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = true,
)
