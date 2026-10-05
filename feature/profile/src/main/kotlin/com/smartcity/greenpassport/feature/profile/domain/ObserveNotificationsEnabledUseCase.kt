package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.messaging.repository.INotificationsRepository
import com.smartcity.greenpassport.core.model.settings.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveNotificationsEnabledUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val notificationsRepository: INotificationsRepository,
) {
    operator fun invoke(): Flow<Boolean> = appSettingsRepository.observeNotificationsEnabled()
        .map { isEnabled -> isEnabled && notificationsRepository.hasNotificationPermission() }
}
