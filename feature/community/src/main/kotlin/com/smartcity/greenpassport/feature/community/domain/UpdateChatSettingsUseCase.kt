package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.community.ChatSettings
import com.smartcity.greenpassport.core.model.community.ChatSettingsRepository
import javax.inject.Inject

class UpdateChatSettingsUseCase @Inject constructor(
    private val chatSettingsRepository: ChatSettingsRepository,
) {
    suspend operator fun invoke(settings: ChatSettings, userId: String) {
        chatSettingsRepository.save(settings, userId)
    }
}
