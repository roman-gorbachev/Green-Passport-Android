package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.core.model.ShopRepository
import javax.inject.Inject

class GetRewardsUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
) {
    suspend operator fun invoke(): List<Reward> = shopRepository.getRewards()
}
