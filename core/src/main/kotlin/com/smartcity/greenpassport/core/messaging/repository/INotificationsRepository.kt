package com.smartcity.greenpassport.core.messaging.repository

interface INotificationsRepository {
    fun hasNotificationPermission(): Boolean
    fun ensureNotificationChannels()
    suspend fun registerToken(userId: String)
    suspend fun unregisterToken()
}
