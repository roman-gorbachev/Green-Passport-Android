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

class CouponReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val couponTitle = inputData.getString(KEY_COUPON_TITLE) ?: return Result.failure()
        val title = applicationContext.getString(R.string.coupon_expires_soon)
        val body = applicationContext.getString(R.string.coupon_valid_until_tomorrow_msg, couponTitle)
        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, NotificationLogEntryPoint::class.java)
        entryPoint.notificationLogRepository().log(title = title, body = body)

        val isEnabled = entryPoint.appSettingsRepository().observeNotificationsEnabled().first()
        val isPermitted = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (isEnabled && isPermitted) {
            val notification = NotificationCompat.Builder(applicationContext, NotificationChannels.REWARDS_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(applicationContext).notify(couponTitle.hashCode(), notification)
        }
        return Result.success()
    }

    companion object {
        const val KEY_COUPON_TITLE = "coupon_title"
    }
}
