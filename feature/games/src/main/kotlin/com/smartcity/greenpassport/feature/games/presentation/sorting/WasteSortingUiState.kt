package com.smartcity.greenpassport.feature.games.presentation.sorting

data class WasteSortingUiState(
    val currentItem: WasteItemDef? = null,
    val score: Int = 0,
    val secondsRemaining: Int = 0,
    val isFinished: Boolean = false,
)
