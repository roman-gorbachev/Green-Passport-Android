package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.ShopRepository
import javax.inject.Inject

class GetPurchasesUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
) {
    suspend operator fun invoke(userId: String): List<Coupon> = shopRepository.getPurchases(userId)
}
