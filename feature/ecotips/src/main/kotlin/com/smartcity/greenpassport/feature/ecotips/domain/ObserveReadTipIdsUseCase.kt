package com.smartcity.greenpassport.feature.ecotips.domain

import com.smartcity.greenpassport.core.model.EcoTipsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveReadTipIdsUseCase @Inject constructor(
    private val ecoTipsRepository: EcoTipsRepository,
) {
    operator fun invoke(userId: String): Flow<Set<String>> = ecoTipsRepository.observeReadTipIds(userId)
}
