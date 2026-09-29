package com.smartcity.greenpassport.feature.games.domain

import com.smartcity.greenpassport.core.datasource.local.repository.GameProgressRepository
import com.smartcity.greenpassport.core.model.rewards.RewardResult
import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import javax.inject.Inject

class SubmitGameResultUseCase @Inject constructor(
    private val gameProgressRepository: GameProgressRepository,
    private val rewardsRepository: RewardsRepository,
) {
    suspend operator fun invoke(gameId: GameId, score: Int): RewardResult {
        gameProgressRepository.recordScore(gameId.storageId, score)
        return rewardsRepository.recordGameResult(gameId.storageId, score)
    }
}
