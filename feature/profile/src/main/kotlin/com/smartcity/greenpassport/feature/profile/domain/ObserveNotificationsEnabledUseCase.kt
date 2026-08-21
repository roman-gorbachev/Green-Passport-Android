package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"

class ObserveNotificationsEnabledUseCase @Inject constructor(
    private val settingsStore: LocalSettingsStore,
) {
    operator fun invoke(): Flow<Boolean> = settingsStore.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
}
