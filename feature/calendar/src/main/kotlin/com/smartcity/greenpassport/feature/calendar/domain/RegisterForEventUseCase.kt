package com.smartcity.greenpassport.feature.calendar.domain

import com.smartcity.greenpassport.core.messaging.helpers.ReminderScheduler
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.EventsRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val REMINDER_OFFSET_MILLIS = TimeUnit.HOURS.toMillis(1)

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
