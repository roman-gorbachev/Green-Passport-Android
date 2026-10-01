package com.smartcity.greenpassport.feature.shop.presentation.state

import com.smartcity.greenpassport.core.model.rewards.RewardFailure

data class CouponDetailUiState(
    val item: CouponItem? = null,
    val qrPayload: String? = null,
    val nowEpochMillis: Long = System.currentTimeMillis(),
    val isLoading: Boolean = true,
    val isMarking: Boolean = false,
    val failure: RewardFailure? = null,
)
