package com.smartcity.greenpassport.core.messaging

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
import com.smartcity.greenpassport.core.database.FirestoreCollections
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

private const val REWARDS_CHANNEL_ID = "greenpassport_rewards"
private const val FIELD_FCM_TOKEN = "fcmToken"

class FirebaseNotificationsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messaging: FirebaseMessaging,
    private val firestore: FirebaseFirestore,
) : NotificationsRepository {

    override fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            REWARDS_CHANNEL_ID,
            context.getString(R.string.notification_channel_rewards_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override suspend fun registerToken(userId: String) {
        val token = messaging.token.await()
        FirestoreCollections.users(firestore).document(userId)
            .set(mapOf(FIELD_FCM_TOKEN to token), SetOptions.merge())
            .await()
    }
}
