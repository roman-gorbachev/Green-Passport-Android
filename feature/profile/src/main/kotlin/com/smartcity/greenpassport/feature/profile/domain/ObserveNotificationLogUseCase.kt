package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.datasource.local.repository.NotificationLogEntry
import com.smartcity.greenpassport.core.datasource.local.repository.NotificationLogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotificationLogUseCase @Inject constructor(
    private val notificationLogRepository: NotificationLogRepository,
) {
    operator fun invoke(): Flow<List<NotificationLogEntry>> = notificationLogRepository.observeAll()
}
