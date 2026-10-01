package com.smartcity.greenpassport.core.messaging.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.smartcity.greenpassport.core.messaging.helpers.ReminderScheduler
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

    override fun scheduleCouponReminder(couponId: String, title: String, expiresAtEpochMillis: Long) {
        val triggerAt = expiresAtEpochMillis - COUPON_REMINDER_LEAD_MILLIS
        val delayMillis = triggerAt - System.currentTimeMillis()
        if (delayMillis <= 0) return
        val request = OneTimeWorkRequestBuilder<CouponReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(CouponReminderWorker.KEY_COUPON_TITLE to title))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            COUPON_WORK_PREFIX + couponId,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    companion object {
        private const val COUPON_REMINDER_LEAD_MILLIS = 24 * 60 * 60 * 1000L
        private const val COUPON_WORK_PREFIX = "coupon_expiring_"
    }
}

private fun uniqueWorkName(eventId: String): String = "event_reminder_$eventId"
