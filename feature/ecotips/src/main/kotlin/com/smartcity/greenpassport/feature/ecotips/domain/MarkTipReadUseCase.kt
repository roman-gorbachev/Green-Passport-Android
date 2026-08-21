package com.smartcity.greenpassport.feature.ecotips.domain

import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipsRepository
import com.smartcity.greenpassport.core.model.PointsAward
import com.smartcity.greenpassport.core.model.PointsEarnReason
import com.smartcity.greenpassport.core.model.PointsRepository
import javax.inject.Inject

class MarkTipReadUseCase @Inject constructor(
    private val ecoTipsRepository: EcoTipsRepository,
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String, tip: EcoTip) {
        ecoTipsRepository.markTipRead(userId, tip.id)
        pointsRepository.award(
            userId = userId,
            award = PointsAward(
                points = tip.rewardPoints,
                xp = tip.rewardXp,
                reason = PointsEarnReason.ARTICLE_READ,
            ),
        )
    }
}
