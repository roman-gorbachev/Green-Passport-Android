package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.settings.AppSettingsRepository
import javax.inject.Inject

class SetNotificationsEnabledUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        appSettingsRepository.setNotificationsEnabled(enabled)
    }
}
