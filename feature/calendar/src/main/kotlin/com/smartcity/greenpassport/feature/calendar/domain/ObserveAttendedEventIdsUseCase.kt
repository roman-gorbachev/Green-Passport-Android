package com.smartcity.greenpassport.feature.calendar.domain

import com.smartcity.greenpassport.core.model.EventsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAttendedEventIdsUseCase @Inject constructor(
    private val eventsRepository: EventsRepository,
) {
    operator fun invoke(userId: String): Flow<Set<String>> = eventsRepository.observeAttendedEventIds(userId)
}
