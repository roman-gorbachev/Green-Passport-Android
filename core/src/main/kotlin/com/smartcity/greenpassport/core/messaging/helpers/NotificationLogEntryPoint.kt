package com.smartcity.greenpassport.core.messaging.helpers

import com.smartcity.greenpassport.core.datasource.local.repository.NotificationLogRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NotificationLogEntryPoint {
    fun notificationLogRepository(): NotificationLogRepository
}
