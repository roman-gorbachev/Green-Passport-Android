package com.smartcity.greenpassport.feature.profile.presentation.bookmarks

import com.smartcity.greenpassport.core.model.EcoTip

data class BookmarksUiState(
    val tips: List<EcoTip> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
