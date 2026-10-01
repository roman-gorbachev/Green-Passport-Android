package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.ShopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCouponUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
) {
    operator fun invoke(couponId: String): Flow<Coupon?> = shopRepository.observePurchase(couponId)
}
