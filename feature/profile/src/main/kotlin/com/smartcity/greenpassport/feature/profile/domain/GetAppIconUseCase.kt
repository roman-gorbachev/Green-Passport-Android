package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.settings.AppIcon
import com.smartcity.greenpassport.core.model.settings.AppIconRepository
import javax.inject.Inject

class GetAppIconUseCase @Inject constructor(
    private val appIconRepository: AppIconRepository,
) {
    operator fun invoke(): AppIcon = appIconRepository.current
}
