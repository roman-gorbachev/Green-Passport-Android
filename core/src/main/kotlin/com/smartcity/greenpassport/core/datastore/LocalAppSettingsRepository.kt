package com.smartcity.greenpassport.core.datastore

import com.smartcity.greenpassport.core.model.settings.AppSettingsRepository
import com.smartcity.greenpassport.core.model.settings.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val KEY_APP_THEME = "app_theme"
private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"

class LocalAppSettingsRepository @Inject constructor(
    private val settingsStore: LocalSettingsStore,
) : AppSettingsRepository {

    override fun observeTheme(): Flow<AppTheme> =
        settingsStore.getString(KEY_APP_THEME, null).map { name ->
            AppTheme.entries.firstOrNull { it.name == name } ?: AppTheme.SYSTEM
        }

    override suspend fun setTheme(theme: AppTheme) {
        settingsStore.setString(KEY_APP_THEME, theme.name)
    }

    override fun observeNotificationsEnabled(): Flow<Boolean> =
        settingsStore.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        settingsStore.setBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
    }
}
