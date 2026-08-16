package com.smartcity.greenpassport.core.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GameProgressDao {
    @Query("SELECT * FROM game_progress WHERE gameId = :gameId")
    suspend fun getProgress(gameId: String): GameProgressEntity?

    @Query("SELECT * FROM game_progress")
    fun observeAll(): Flow<List<GameProgressEntity>>

    @Upsert
    suspend fun upsert(progress: GameProgressEntity)
}
