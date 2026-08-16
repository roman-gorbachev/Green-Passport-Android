package com.smartcity.greenpassport.core.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_progress")
data class GameProgressEntity(
    @PrimaryKey val gameId: String,
    val bestScore: Int,
    val lastPlayedAtEpochMillis: Long,
)
