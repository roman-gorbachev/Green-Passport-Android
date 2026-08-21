package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipsRepository
import com.smartcity.greenpassport.core.model.FavoritesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBookmarkedTipsUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val ecoTipsRepository: EcoTipsRepository,
) {
    suspend operator fun invoke(userId: String): List<EcoTip> {
        val bookmarkedIds = favoritesRepository.observeBookmarkedTipIds(userId).first()
        return ecoTipsRepository.getTips().filter { bookmarkedIds.contains(it.id) }
    }
}
