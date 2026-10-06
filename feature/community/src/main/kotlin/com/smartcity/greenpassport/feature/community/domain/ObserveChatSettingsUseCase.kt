package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.model.community.ChatSettings
import com.smartcity.greenpassport.core.model.community.ChatSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChatSettingsUseCase @Inject constructor(
    private val chatSettingsRepository: ChatSettingsRepository,
) {
    operator fun invoke(userId: String): Flow<Map<ChatId, ChatSettings>> = chatSettingsRepository.observeSettings(
        userId
    )
}
