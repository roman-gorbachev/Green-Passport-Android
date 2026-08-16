package com.smartcity.greenpassport.core.messaging

interface ReminderScheduler {
    fun scheduleEventReminder(eventId: String, eventTitle: String, triggerAtEpochMillis: Long)
    fun cancelEventReminder(eventId: String)
}
