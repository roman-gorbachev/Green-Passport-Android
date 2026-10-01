package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.core.model.ShopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRewardsUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
) {
    operator fun invoke(): Flow<List<Reward>> = shopRepository.observeRewards()
}
