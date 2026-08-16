package com.smartcity.greenpassport.core.local

import kotlinx.coroutines.flow.Flow

data class GameProgress(
    val gameId: String,
    val bestScore: Int,
    val lastPlayedAtEpochMillis: Long,
)

interface GameProgressRepository {
    suspend fun getBestScore(gameId: String): Int
    suspend fun recordScore(gameId: String, score: Int)
    fun observeAll(): Flow<List<GameProgress>>
}
