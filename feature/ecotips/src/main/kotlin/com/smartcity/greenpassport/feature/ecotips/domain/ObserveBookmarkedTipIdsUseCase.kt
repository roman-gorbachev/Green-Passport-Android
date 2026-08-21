package com.smartcity.greenpassport.feature.ecotips.domain

import com.smartcity.greenpassport.core.model.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBookmarkedTipIdsUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(userId: String): Flow<Set<String>> = favoritesRepository.observeBookmarkedTipIds(userId)
}
