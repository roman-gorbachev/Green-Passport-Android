package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.settings.AppSettingsRepository
import com.smartcity.greenpassport.core.model.settings.AppTheme
import javax.inject.Inject

class SetAppThemeUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) {
    suspend operator fun invoke(theme: AppTheme) {
        appSettingsRepository.setTheme(theme)
    }
}
