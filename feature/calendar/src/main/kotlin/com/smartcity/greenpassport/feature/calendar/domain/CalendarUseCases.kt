package com.smartcity.greenpassport.feature.calendar.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.messaging.ReminderScheduler
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.EventsRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

private val REMINDER_OFFSET_MILLIS = TimeUnit.HOURS.toMillis(1)

class ObserveCalendarSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.session
}

class GetEventsUseCase @Inject constructor(
    private val eventsRepository: EventsRepository,
) {
    suspend operator fun invoke(): List<EcoEvent> = eventsRepository.getEvents().sortedBy { it.startAtEpochMillis }
}

class GetRegisteredEventIdsUseCase @Inject constructor(
    private val eventsRepository: EventsRepository,
) {
    suspend operator fun invoke(userId: String): Set<String> = eventsRepository.getRegisteredEventIds(userId)
}

class RegisterForEventUseCase @Inject constructor(
    private val eventsRepository: EventsRepository,
    private val reminderScheduler: ReminderScheduler,
) {
    suspend operator fun invoke(userId: String, event: EcoEvent) {
        eventsRepository.registerForEvent(userId, event.id)
        reminderScheduler.scheduleEventReminder(
            eventId = event.id,
            eventTitle = event.title,
            triggerAtEpochMillis = event.startAtEpochMillis - REMINDER_OFFSET_MILLIS,
        )
    }
}
