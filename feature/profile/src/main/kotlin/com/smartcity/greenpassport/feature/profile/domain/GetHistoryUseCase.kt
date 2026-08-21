package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.HistoryEntry
import com.smartcity.greenpassport.core.model.HistoryRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(userId: String): List<HistoryEntry> = historyRepository.getHistory(userId)
}
