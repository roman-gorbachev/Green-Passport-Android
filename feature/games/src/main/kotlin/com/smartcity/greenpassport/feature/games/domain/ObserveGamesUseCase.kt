package com.smartcity.greenpassport.feature.games.domain

import com.smartcity.greenpassport.core.model.games.Game
import com.smartcity.greenpassport.core.model.games.GamesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGamesUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
) {
    operator fun invoke(): Flow<List<Game>> = gamesRepository.observeGames()
}
