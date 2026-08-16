package com.smartcity.greenpassport.feature.ecotips.presentation

import com.smartcity.greenpassport.core.model.EcoTip

data class EcoTipDetailUiState(
    val tip: EcoTip? = null,
    val isRead: Boolean = false,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
)
