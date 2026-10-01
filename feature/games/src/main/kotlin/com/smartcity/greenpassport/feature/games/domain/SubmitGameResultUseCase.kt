package com.smartcity.greenpassport.feature.games.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.datasource.local.repository.GameProgressRepository
import com.smartcity.greenpassport.core.model.rewards.RewardResult
import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SubmitGameResultUseCase @Inject constructor(
    private val gameProgressRepository: GameProgressRepository,
    private val rewardsRepository: RewardsRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(gameId: String, score: Int): RewardResult? {
        gameProgressRepository.recordScore(gameId, score)
        if (score <= 0 || authRepository.session.first() == null) return null
        return rewardsRepository.recordGameResult(gameId, score)
    }
}
