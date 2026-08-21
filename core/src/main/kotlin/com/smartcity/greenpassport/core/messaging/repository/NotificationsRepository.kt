package com.smartcity.greenpassport.core.messaging.repository

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.smartcity.greenpassport.core.R
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.messaging.helpers.NotificationChannels
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_FCM_TOKEN = "fcmToken"

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

    override fun ensureNotificationChannel() {
        val channel = NotificationChannel(
            NotificationChannels.REWARDS_CHANNEL_ID,
            context.getString(R.string.notification_channel_rewards_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    @Suppress("DEPRECATION")
    override suspend fun registerToken(userId: String) {
        val token = messaging.token.await()
        FirestoreCollections.users(firestore).document(userId)
            .set(mapOf(FIELD_FCM_TOKEN to token), SetOptions.merge())
            .await()
    }
}
