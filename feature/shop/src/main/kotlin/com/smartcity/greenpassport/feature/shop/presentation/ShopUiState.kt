package com.smartcity.greenpassport.feature.shop.presentation

import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.Reward

data class ShopUiState(
    val points: Int = 0,
    val rewards: List<Reward> = emptyList(),
    val purchases: List<Coupon> = emptyList(),
    val purchasingRewardId: String? = null,
    val hasInsufficientPoints: Boolean = false,
    val isLoading: Boolean = true,
)
