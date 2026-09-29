package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.settings.AppLanguage
import com.smartcity.greenpassport.core.model.settings.AppLanguageRepository
import javax.inject.Inject

class GetAppLanguageUseCase @Inject constructor(
    private val appLanguageRepository: AppLanguageRepository,
) {
    operator fun invoke(): AppLanguage = appLanguageRepository.getLanguage()
}
