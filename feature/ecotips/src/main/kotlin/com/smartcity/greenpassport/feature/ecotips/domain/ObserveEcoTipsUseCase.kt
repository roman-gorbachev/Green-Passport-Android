package com.smartcity.greenpassport.feature.ecotips.domain

import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveEcoTipsUseCase @Inject constructor(
    private val ecoTipsRepository: EcoTipsRepository,
) {
    operator fun invoke(): Flow<List<EcoTip>> =
        ecoTipsRepository.observeTips().map { tips -> tips.filter { it.isActive } }
}
