package com.smartcity.greenpassport.feature.tasks.domain

import com.smartcity.greenpassport.core.model.FavoritesRepository
import javax.inject.Inject

class ToggleTaskFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(userId: String, taskId: String, isFavorite: Boolean) =
        favoritesRepository.setTaskFavorite(userId, taskId, isFavorite)
}
