package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.settings.AppIcon
import com.smartcity.greenpassport.core.model.settings.AppIconRepository
import javax.inject.Inject

class SetAppIconUseCase @Inject constructor(
    private val appIconRepository: AppIconRepository,
) {
    operator fun invoke(icon: AppIcon) = appIconRepository.set(icon)
}
