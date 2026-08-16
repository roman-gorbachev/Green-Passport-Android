package com.smartcity.greenpassport.feature.games.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.local.GameProgress
import com.smartcity.greenpassport.core.local.GameProgressRepository
import com.smartcity.greenpassport.core.model.PointsAward
import com.smartcity.greenpassport.core.model.PointsEarnReason
import com.smartcity.greenpassport.core.model.PointsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveGamesSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.session
}

class ObserveGameProgressUseCase @Inject constructor(
    private val gameProgressRepository: GameProgressRepository,
) {
    operator fun invoke(): Flow<List<GameProgress>> = gameProgressRepository.observeAll()
}

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
