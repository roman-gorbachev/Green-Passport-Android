package com.smartcity.greenpassport.feature.ecotips.domain

import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.rewards.RewardResult
import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import javax.inject.Inject

class MarkTipReadUseCase @Inject constructor(
    private val rewardsRepository: RewardsRepository,
) {
    suspend operator fun invoke(tip: EcoTip): RewardResult = rewardsRepository.recordTipRead(tip.id)
}
