package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.settings.AppSettingsRepository
import com.smartcity.greenpassport.core.model.settings.AppTheme
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAppThemeUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) {
    operator fun invoke(): Flow<AppTheme> = appSettingsRepository.observeTheme()
}
