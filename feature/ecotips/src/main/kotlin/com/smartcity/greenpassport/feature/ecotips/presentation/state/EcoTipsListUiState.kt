package com.smartcity.greenpassport.feature.ecotips.presentation.state

import com.smartcity.greenpassport.core.common.matchesSearchQuery
import com.smartcity.greenpassport.core.common.resolveForDeviceLanguage
import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipCategory

data class EcoTipsListUiState(
    val tips: List<EcoTip> = emptyList(),
    val readTipIds: Set<String> = emptySet(),
    val bookmarkedTipIds: Set<String> = emptySet(),
    val selectedCategory: EcoTipCategory? = null,
    val query: String = "",
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
) {
    val isSearching: Boolean
        get() = query.isNotBlank()

    val dailyTip: EcoTip?
        get() = if (isSearching) null else tips.firstOrNull { it.isDailyTip }

    val visibleTips: List<EcoTip>
        get() = tips.filter { tip ->
            (selectedCategory == null || tip.category == selectedCategory) &&
                tip.title.resolveForDeviceLanguage().matchesSearchQuery(query)
        }
}
