package com.smartcity.greenpassport.core.model.community

import kotlinx.coroutines.flow.Flow

interface ChatSettingsRepository {
    fun observeSettings(userId: String): Flow<Map<ChatId, ChatSettings>>
    suspend fun save(settings: ChatSettings, userId: String)
}
