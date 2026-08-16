package com.smartcity.greenpassport.feature.ecotips.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipsRepository
import com.smartcity.greenpassport.core.model.FavoritesRepository
import com.smartcity.greenpassport.core.model.PointsAward
import com.smartcity.greenpassport.core.model.PointsEarnReason
import com.smartcity.greenpassport.core.model.PointsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveEcoTipsSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.session
}

class GetEcoTipsUseCase @Inject constructor(
    private val ecoTipsRepository: EcoTipsRepository,
) {
    suspend operator fun invoke(): List<EcoTip> = ecoTipsRepository.getTips()
}

class GetReadTipIdsUseCase @Inject constructor(
    private val ecoTipsRepository: EcoTipsRepository,
) {
    suspend operator fun invoke(userId: String): Set<String> = ecoTipsRepository.getReadTipIds(userId)
}

class MarkTipReadUseCase @Inject constructor(
    private val ecoTipsRepository: EcoTipsRepository,
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String, tip: EcoTip) {
        ecoTipsRepository.markTipRead(userId, tip.id)
        pointsRepository.award(
            userId = userId,
            award = PointsAward(
                points = tip.rewardPoints,
                xp = tip.rewardXp,
                reason = PointsEarnReason.ARTICLE_READ,
            ),
        )
    }
}

class ObserveBookmarkedTipIdsUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(userId: String): Flow<Set<String>> = favoritesRepository.observeBookmarkedTipIds(userId)
}

class ToggleTipBookmarkUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(userId: String, tipId: String, isBookmarked: Boolean) =
        favoritesRepository.setTipBookmarked(userId, tipId, isBookmarked)
}
