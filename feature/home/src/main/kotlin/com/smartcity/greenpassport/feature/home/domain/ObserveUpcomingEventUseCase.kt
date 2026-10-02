package com.smartcity.greenpassport.feature.home.domain

import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.EventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveUpcomingEventUseCase @Inject constructor(
    private val eventsRepository: EventsRepository,
) {
    operator fun invoke(): Flow<EcoEvent?> = eventsRepository.observeEvents().map { events ->
        val now = System.currentTimeMillis()
        events.filter { it.isActive && it.startAtEpochMillis > now }.minByOrNull { it.startAtEpochMillis }
    }
}
