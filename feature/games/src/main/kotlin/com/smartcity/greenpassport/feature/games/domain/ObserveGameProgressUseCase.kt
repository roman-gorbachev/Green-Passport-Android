package com.smartcity.greenpassport.feature.games.domain

import com.smartcity.greenpassport.core.datasource.local.repository.GameProgress
import com.smartcity.greenpassport.core.datasource.local.repository.GameProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGameProgressUseCase @Inject constructor(
    private val gameProgressRepository: GameProgressRepository,
) {
    operator fun invoke(): Flow<List<GameProgress>> = gameProgressRepository.observeAll()
}
