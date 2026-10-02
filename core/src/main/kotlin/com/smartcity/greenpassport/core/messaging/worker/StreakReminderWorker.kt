package com.smartcity.greenpassport.core.messaging.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartcity.greenpassport.core.R
import com.smartcity.greenpassport.core.messaging.helpers.NotificationChannels
import com.smartcity.greenpassport.core.messaging.helpers.NotificationLogEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

class StreakReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val streakDays = inputData.getInt(KEY_STREAK_DAYS, 0)
        if (streakDays <= 0) return Result.success()
        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, NotificationLogEntryPoint::class.java)
        val isEnabled = entryPoint.appSettingsRepository().observeNotificationsEnabled().first()
        val isPermitted = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (isEnabled && isPermitted) {
            val notification = NotificationCompat.Builder(applicationContext, NotificationChannels.REWARDS_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(applicationContext.getString(R.string.streak_reminder_title))
                .setContentText(applicationContext.getString(R.string.streak_reminder_body, streakDays))
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(applicationContext).notify(STREAK_NOTIFICATION_ID, notification)
        }
        return Result.success()
    }

    companion object {
        const val KEY_STREAK_DAYS = "streak_days"
        private const val STREAK_NOTIFICATION_ID = 7_001
    }
}
