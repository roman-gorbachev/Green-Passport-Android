package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.messaging.repository.INotificationsRepository
import com.smartcity.greenpassport.core.model.settings.AppSettingsRepository
import javax.inject.Inject

class SetNotificationsEnabledUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val notificationsRepository: INotificationsRepository,
) {
    suspend operator fun invoke(enabled: Boolean): Boolean {
        val actuallyEnabled = enabled && notificationsRepository.hasNotificationPermission()
        appSettingsRepository.setNotificationsEnabled(actuallyEnabled)
        return actuallyEnabled
    }
}
