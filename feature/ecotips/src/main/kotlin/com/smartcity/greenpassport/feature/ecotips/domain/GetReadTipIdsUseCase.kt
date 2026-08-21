package com.smartcity.greenpassport.feature.ecotips.domain

import com.smartcity.greenpassport.core.model.EcoTipsRepository
import javax.inject.Inject

class GetReadTipIdsUseCase @Inject constructor(
    private val ecoTipsRepository: EcoTipsRepository,
) {
    suspend operator fun invoke(userId: String): Set<String> = ecoTipsRepository.getReadTipIds(userId)
}
