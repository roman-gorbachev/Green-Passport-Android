package com.smartcity.greenpassport.feature.home.domain

import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.EventsRepository
import javax.inject.Inject

class GetUpcomingEventUseCase @Inject constructor(
    private val eventsRepository: EventsRepository,
) {
    suspend operator fun invoke(): EcoEvent? {
        val now = System.currentTimeMillis()
        return eventsRepository.getEvents()
            .filter { it.startAtEpochMillis > now }
            .minByOrNull { it.startAtEpochMillis }
    }
}
