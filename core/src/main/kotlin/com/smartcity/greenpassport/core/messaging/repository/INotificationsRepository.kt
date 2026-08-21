package com.smartcity.greenpassport.core.messaging.repository

interface INotificationsRepository {
    fun hasNotificationPermission(): Boolean
    fun ensureNotificationChannel()
    suspend fun registerToken(userId: String)
}
