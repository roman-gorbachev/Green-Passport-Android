package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.messaging.repository.INotificationsRepository
import com.smartcity.greenpassport.core.model.community.MessageNotificationsRepository
import com.smartcity.greenpassport.core.model.settings.AppSettingsRepository
import com.smartcity.greenpassport.core.model.settings.NotificationCategory
import javax.inject.Inject

class SetNotificationCategoryEnabledUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val messageNotificationsRepository: MessageNotificationsRepository,
    private val notificationsRepository: INotificationsRepository,
) {
    suspend operator fun invoke(category: NotificationCategory, enabled: Boolean, userId: String?) {
        val actuallyEnabled = enabled && notificationsRepository.hasNotificationPermission()
        if (category == NotificationCategory.MESSAGES && userId != null) {
            messageNotificationsRepository.setEnabled(actuallyEnabled, userId)
        } else {
            appSettingsRepository.setNotificationCategoryEnabled(category, actuallyEnabled)
        }
    }
}
