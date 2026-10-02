package com.smartcity.greenpassport.feature.shop.domain

import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.core.model.ShopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveRewardCatalogUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
) {
    operator fun invoke(): Flow<List<Reward>> =
        shopRepository.observeRewards().map { rewards -> rewards.filter { it.isActive } }
}
