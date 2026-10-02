package com.smartcity.greenpassport.feature.home.domain

import com.smartcity.greenpassport.core.messaging.helpers.ReminderScheduler
import com.smartcity.greenpassport.core.model.Streak
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class UpdateStreakReminderUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler,
) {
    operator fun invoke(streak: Streak?, nowEpochMillis: Long) {
        val fireAt = reminderTime(nowEpochMillis)
        if (streak != null && fireAt > nowEpochMillis && streak.isAtRisk(nowEpochMillis)) {
            reminderScheduler.scheduleStreakReminder(streak.count, fireAt)
        } else {
            reminderScheduler.cancelStreakReminder()
        }
    }

    private fun Streak.isAtRisk(nowEpochMillis: Long): Boolean =
        currentCount(nowEpochMillis) > 0 && !isCounted(nowEpochMillis)

    private fun reminderTime(nowEpochMillis: Long): Long =
        Instant.ofEpochMilli(nowEpochMillis)
            .atZone(SERVER_ZONE)
            .toLocalDate()
            .atTime(LocalTime.of(REMINDER_HOUR, 0))
            .atZone(SERVER_ZONE)
            .toInstant()
            .toEpochMilli()

    private companion object {
        const val REMINDER_HOUR = 20
        val SERVER_ZONE: ZoneId = ZoneId.of("Europe/Minsk")
    }
}
