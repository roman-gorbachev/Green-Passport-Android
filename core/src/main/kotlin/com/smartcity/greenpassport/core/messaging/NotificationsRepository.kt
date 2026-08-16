package com.smartcity.greenpassport.core.messaging

interface NotificationsRepository {
    fun hasNotificationPermission(): Boolean
    fun ensureNotificationChannel()
    suspend fun registerToken(userId: String)
}
