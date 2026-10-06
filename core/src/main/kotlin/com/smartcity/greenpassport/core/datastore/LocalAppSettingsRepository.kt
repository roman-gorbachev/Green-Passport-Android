package com.smartcity.greenpassport.core.datastore

import com.smartcity.greenpassport.core.model.settings.AppSettingsRepository
import com.smartcity.greenpassport.core.model.settings.AppTheme
import com.smartcity.greenpassport.core.model.settings.NotificationCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val KEY_APP_THEME = "app_theme"
private const val KEY_LEGACY_NOTIFICATIONS_ENABLED = "notifications_enabled"

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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNotificationCategoryEnabled(category: NotificationCategory): Flow<Boolean> =
        settingsStore.getBoolean(KEY_LEGACY_NOTIFICATIONS_ENABLED, true).flatMapLatest { legacyValue ->
            settingsStore.getBoolean(category.key, legacyValue)
        }

    override suspend fun setNotificationCategoryEnabled(category: NotificationCategory, enabled: Boolean) {
        settingsStore.setBoolean(category.key, enabled)
    }

    private val NotificationCategory.key: String
        get() = "notifications_${name.lowercase()}_enabled"
}
