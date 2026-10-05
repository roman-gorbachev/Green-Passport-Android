package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.FavoritesRepository
import javax.inject.Inject

class RemoveTipBookmarkUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(userId: String, tipId: String) =
        favoritesRepository.setTipBookmarked(userId, tipId, isBookmarked = false)
}
