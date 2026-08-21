package com.smartcity.greenpassport.feature.ecotips.domain

import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipsRepository
import javax.inject.Inject

class GetEcoTipsUseCase @Inject constructor(
    private val ecoTipsRepository: EcoTipsRepository,
) {
    suspend operator fun invoke(): List<EcoTip> = ecoTipsRepository.getTips()
}
