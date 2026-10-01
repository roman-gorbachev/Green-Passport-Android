package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.ShopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePurchasesUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
) {
    operator fun invoke(userId: String): Flow<List<Coupon>> = shopRepository.observePurchases(userId)
}
