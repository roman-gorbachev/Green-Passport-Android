package com.smartcity.greenpassport.feature.ecotips.presentation

import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipCategory

data class EcoTipsListUiState(
    val tips: List<EcoTip> = emptyList(),
    val readTipIds: Set<String> = emptySet(),
    val bookmarkedTipIds: Set<String> = emptySet(),
    val selectedCategory: EcoTipCategory? = null,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
) {
    val dailyTip: EcoTip?
        get() = tips.firstOrNull { it.isDailyTip }

    val visibleTips: List<EcoTip>
        get() = tips.filter { selectedCategory == null || it.category == selectedCategory }
}
