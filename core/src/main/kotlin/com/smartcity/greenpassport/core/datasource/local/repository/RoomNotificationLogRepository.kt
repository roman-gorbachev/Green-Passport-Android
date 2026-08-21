package com.smartcity.greenpassport.core.datasource.local.repository

import com.smartcity.greenpassport.core.datasource.local.dao.NotificationLogDao
import com.smartcity.greenpassport.core.datasource.local.entities.NotificationLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomNotificationLogRepository @Inject constructor(
    private val dao: NotificationLogDao,
) : NotificationLogRepository {

    override suspend fun log(title: String, body: String) {
        dao.insert(
            NotificationLogEntity(
                title = title,
                body = body,
                sentAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }

    override fun observeAll(): Flow<List<NotificationLogEntry>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }
}

private fun NotificationLogEntity.toDomain() = NotificationLogEntry(
    id = id,
    title = title,
    body = body,
    sentAtEpochMillis = sentAtEpochMillis,
)
