package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.community.ForwardOrigin
import com.smartcity.greenpassport.core.model.community.MessageQuote
import com.smartcity.greenpassport.core.model.profile.UserProfileRepository
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.core.moderation.TextModerator
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SendGroupMessageUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val userProfileRepository: UserProfileRepository,
    private val textModerator: TextModerator,
) {
    suspend operator fun invoke(
        groupId: String,
        senderId: String,
        text: String,
        replyTo: MessageQuote? = null,
        forwardedFrom: ForwardOrigin? = null,
    ) {
        if (!textModerator.isAllowed(text)) throw ContentRejectedException()
        val profile = userProfileRepository.observeProfile(senderId).catch { emit(null) }.first()
        communityRepository.sendMessage(
            groupId = groupId,
            senderId = senderId,
            senderName = profile?.let { "${it.firstName} ${it.lastName}".trim() }?.ifBlank { null },
            senderAvatar = profile?.avatar,
            text = text,
            replyTo = replyTo,
            forwardedFrom = forwardedFrom,
        )
    }
}
