package com.smartcity.greenpassport.core.local

import kotlinx.coroutines.flow.Flow

data class NotificationLogEntry(
    val id: Long,
    val title: String,
    val body: String,
    val sentAtEpochMillis: Long,
)

interface NotificationLogRepository {
    suspend fun log(title: String, body: String)
    fun observeAll(): Flow<List<NotificationLogEntry>>
}
