package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.FavoritesRepository
import javax.inject.Inject

class RemoveFavoriteTaskUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(userId: String, taskId: String) =
        favoritesRepository.setTaskFavorite(userId, taskId, isFavorite = false)
}
