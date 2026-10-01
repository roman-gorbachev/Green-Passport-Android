package com.smartcity.greenpassport.feature.calendar.domain

import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.EventsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveEventsUseCase @Inject constructor(
    private val eventsRepository: EventsRepository,
) {
    operator fun invoke(): Flow<List<EcoEvent>> = eventsRepository.observeEvents()
}
