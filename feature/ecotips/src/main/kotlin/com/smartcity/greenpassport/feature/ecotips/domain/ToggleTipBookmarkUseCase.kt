package com.smartcity.greenpassport.feature.ecotips.domain

import com.smartcity.greenpassport.core.model.FavoritesRepository
import javax.inject.Inject

class ToggleTipBookmarkUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(userId: String, tipId: String, isBookmarked: Boolean) =
        favoritesRepository.setTipBookmarked(userId, tipId, isBookmarked)
}
