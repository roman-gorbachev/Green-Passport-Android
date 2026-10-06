package com.smartcity.greenpassport.core.model.settings

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun observeTheme(): Flow<AppTheme>
    suspend fun setTheme(theme: AppTheme)
    fun observeNotificationCategoryEnabled(category: NotificationCategory): Flow<Boolean>
    suspend fun setNotificationCategoryEnabled(category: NotificationCategory, enabled: Boolean)
}
