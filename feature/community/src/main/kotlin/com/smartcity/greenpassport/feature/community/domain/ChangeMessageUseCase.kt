package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.model.community.MessageEditingRepository
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.core.moderation.TextModerator
import javax.inject.Inject

class ChangeMessageUseCase @Inject constructor(
    private val messageEditingRepository: MessageEditingRepository,
    private val textModerator: TextModerator,
) {
    suspend operator fun invoke(chat: ChatId, messageId: String, change: MessageChange) {
        when (change) {
            is MessageChange.Edit -> {
                if (!textModerator.isAllowed(change.text)) throw ContentRejectedException()
                messageEditingRepository.editMessage(chat, messageId, change.text)
            }
            MessageChange.Delete -> messageEditingRepository.deleteMessage(chat, messageId)
        }
    }
}
