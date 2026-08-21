package com.smartcity.greenpassport.feature.calendar.domain

import com.smartcity.greenpassport.core.model.EventsRepository
import javax.inject.Inject

class GetRegisteredEventIdsUseCase @Inject constructor(
    private val eventsRepository: EventsRepository,
) {
    suspend operator fun invoke(userId: String): Set<String> = eventsRepository.getRegisteredEventIds(userId)
}
