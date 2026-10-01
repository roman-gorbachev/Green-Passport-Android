package com.smartcity.greenpassport.core.model.settings

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun observeTheme(): Flow<AppTheme>
    suspend fun setTheme(theme: AppTheme)
    fun observeNotificationsEnabled(): Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean)
}
