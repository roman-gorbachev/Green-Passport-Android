package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.settings.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotificationsEnabledUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) {
    operator fun invoke(): Flow<Boolean> = appSettingsRepository.observeNotificationsEnabled()
}
