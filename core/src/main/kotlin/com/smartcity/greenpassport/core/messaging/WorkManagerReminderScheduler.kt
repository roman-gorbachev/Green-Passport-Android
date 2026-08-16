package com.smartcity.greenpassport.core.messaging

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : ReminderScheduler {

    override fun scheduleEventReminder(eventId: String, eventTitle: String, triggerAtEpochMillis: Long) {
        val delayMillis = (triggerAtEpochMillis - System.currentTimeMillis()).coerceAtLeast(0)
        val request = OneTimeWorkRequestBuilder<EventReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    EventReminderWorker.KEY_EVENT_ID to eventId,
                    EventReminderWorker.KEY_EVENT_TITLE to eventTitle,
                ),
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueWorkName(eventId),
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    override fun cancelEventReminder(eventId: String) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName(eventId))
    }
}

private fun uniqueWorkName(eventId: String): String = "event_reminder_$eventId"
