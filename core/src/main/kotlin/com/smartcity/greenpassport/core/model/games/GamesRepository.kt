package com.smartcity.greenpassport.core.model.games

import kotlinx.coroutines.flow.Flow

interface GamesRepository {
    fun observeGames(): Flow<List<Game>>
}
