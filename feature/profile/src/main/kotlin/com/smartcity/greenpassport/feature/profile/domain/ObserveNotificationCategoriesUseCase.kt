package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.messaging.repository.INotificationsRepository
import com.smartcity.greenpassport.core.model.community.MessageNotificationsRepository
import com.smartcity.greenpassport.core.model.settings.AppSettingsRepository
import com.smartcity.greenpassport.core.model.settings.NotificationCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveNotificationCategoriesUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val messageNotificationsRepository: MessageNotificationsRepository,
    private val notificationsRepository: INotificationsRepository,
) {
    operator fun invoke(userId: String?): Flow<Set<NotificationCategory>> {
        val messages = userId
            ?.let { messageNotificationsRepository.observeIsEnabled(it).catch { emit(true) } }
            ?: flowOf(true)
        return combine(
            appSettingsRepository.observeNotificationCategoryEnabled(NotificationCategory.EVENTS),
            appSettingsRepository.observeNotificationCategoryEnabled(NotificationCategory.TASKS),
            messages,
        ) { events, tasks, chats ->
            if (!notificationsRepository.hasNotificationPermission()) {
                emptySet()
            } else {
                setOfNotNull(
                    NotificationCategory.EVENTS.takeIf { events },
                    NotificationCategory.TASKS.takeIf { tasks },
                    NotificationCategory.MESSAGES.takeIf { chats },
                )
            }
        }
    }
}
