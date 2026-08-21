package com.smartcity.greenpassport.core.datasource.local.repository

import com.smartcity.greenpassport.core.datasource.local.dao.GameProgressDao
import com.smartcity.greenpassport.core.datasource.local.entities.GameProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomGameProgressRepository @Inject constructor(
    private val dao: GameProgressDao,
) : GameProgressRepository {

    override suspend fun getBestScore(gameId: String): Int =
        dao.getProgress(gameId)?.bestScore ?: 0

    override suspend fun recordScore(gameId: String, score: Int) {
        val current = dao.getProgress(gameId)
        val newBestScore = maxOf(current?.bestScore ?: 0, score)
        dao.upsert(
            GameProgressEntity(
                gameId = gameId,
                bestScore = newBestScore,
                lastPlayedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }

    override fun observeAll(): Flow<List<GameProgress>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }
}

private fun GameProgressEntity.toDomain() = GameProgress(
    gameId = gameId,
    bestScore = bestScore,
    lastPlayedAtEpochMillis = lastPlayedAtEpochMillis,
)
