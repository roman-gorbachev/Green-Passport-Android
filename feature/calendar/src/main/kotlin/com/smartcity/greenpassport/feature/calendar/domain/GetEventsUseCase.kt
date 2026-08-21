package com.smartcity.greenpassport.feature.calendar.domain

import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.EventsRepository
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val eventsRepository: EventsRepository,
) {
    suspend operator fun invoke(): List<EcoEvent> = eventsRepository.getEvents().sortedBy { it.startAtEpochMillis }
}
