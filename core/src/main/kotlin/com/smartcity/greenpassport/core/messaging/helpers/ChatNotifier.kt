package com.smartcity.greenpassport.core.messaging.helpers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.smartcity.greenpassport.core.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ChatNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun show(chatId: String, chatTitle: String?, body: String) {
        val isPermitted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!isPermitted) return
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?.putExtra(EXTRA_CHAT_ID, chatId)
            ?.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            ?: return
        val pendingIntent = PendingIntent.getActivity(
            context,
            chatId.hashCode(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val title = chatTitle?.takeIf { it.isNotBlank() } ?: context.getString(R.string.community_forum_title)
        val notification = NotificationCompat.Builder(context, NotificationChannels.MESSAGES_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setGroup(chatId)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(chatId, CHAT_NOTIFICATION_ID, notification)
    }

    companion object {
        const val EXTRA_CHAT_ID = "com.smartcity.greenpassport.CHAT_ID"
        private const val CHAT_NOTIFICATION_ID = 1
    }
}
