package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import javax.inject.Inject

private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"

class SetNotificationsEnabledUseCase @Inject constructor(
    private val settingsStore: LocalSettingsStore,
) {
    suspend operator fun invoke(enabled: Boolean) =
        settingsStore.setBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
}
