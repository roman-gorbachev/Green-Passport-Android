package com.smartcity.greenpassport.core.messaging.helpers

interface ReminderScheduler {
    fun scheduleEventReminder(eventId: String, eventTitle: String, triggerAtEpochMillis: Long)
    fun cancelEventReminder(eventId: String)
    fun scheduleCouponReminder(couponId: String, title: String, expiresAtEpochMillis: Long)
    fun scheduleStreakReminder(streakDays: Int, triggerAtEpochMillis: Long)
    fun cancelStreakReminder()
}
