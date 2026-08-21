package com.smartcity.greenpassport.feature.games.domain

import com.smartcity.greenpassport.core.datasource.local.repository.GameProgressRepository
import com.smartcity.greenpassport.core.model.PointsAward
import com.smartcity.greenpassport.core.model.PointsEarnReason
import com.smartcity.greenpassport.core.model.PointsRepository
import javax.inject.Inject

class SubmitGameResultUseCase @Inject constructor(
    private val gameProgressRepository: GameProgressRepository,
    private val pointsRepository: PointsRepository,
) {
    suspend operator fun invoke(userId: String, gameId: GameId, score: Int) {
        gameProgressRepository.recordScore(gameId.storageId, score)
        pointsRepository.award(
            userId = userId,
            award = PointsAward(points = score, xp = score, reason = PointsEarnReason.GAME_PLAYED),
        )
    }
}
