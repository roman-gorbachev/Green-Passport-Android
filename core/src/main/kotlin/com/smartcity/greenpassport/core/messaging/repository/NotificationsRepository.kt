package com.smartcity.greenpassport.core.messaging.repository

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.smartcity.greenpassport.core.R
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.messaging.helpers.NotificationChannels
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_USER_ID = "userId"
private const val FIELD_PLATFORM = "platform"
private const val FIELD_UPDATED_AT = "updatedAtEpochMillis"
private const val PLATFORM_ANDROID = "ANDROID"

class NotificationsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messaging: FirebaseMessaging,
    private val firestore: FirebaseFirestore,
) : INotificationsRepository {

    override fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun ensureNotificationChannels() {
        val channels = listOf(
            NotificationChannel(
                NotificationChannels.REWARDS_CHANNEL_ID,
                context.getString(R.string.notification_channel_rewards_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
            NotificationChannel(
                NotificationChannels.MESSAGES_CHANNEL_ID,
                context.getString(R.string.notification_channel_messages),
                NotificationManager.IMPORTANCE_HIGH,
            ),
        )
        context.getSystemService(NotificationManager::class.java)?.createNotificationChannels(channels)
    }

    override suspend fun registerToken(userId: String) {
        val token = messaging.token.await()
        val data = mapOf(
            FIELD_USER_ID to userId,
            FIELD_PLATFORM to PLATFORM_ANDROID,
            FIELD_UPDATED_AT to System.currentTimeMillis(),
        )
        FirestoreCollections.userDevices(firestore).document(token).set(data).await()
    }

    override suspend fun unregisterToken() {
        val token = messaging.token.await()
        FirestoreCollections.userDevices(firestore).document(token).delete().await()
    }
}
