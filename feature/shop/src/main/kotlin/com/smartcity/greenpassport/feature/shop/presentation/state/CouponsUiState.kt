package com.smartcity.greenpassport.feature.shop.presentation.state

import com.smartcity.greenpassport.core.model.CouponStatus

data class CouponsUiState(
    val items: List<CouponItem> = emptyList(),
    val nowEpochMillis: Long = System.currentTimeMillis(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
) {
    fun items(status: CouponStatus): List<CouponItem> = items.filter { it.coupon.status(nowEpochMillis) == status }
}
