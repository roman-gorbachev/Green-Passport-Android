package com.smartcity.greenpassport.feature.community.domain

import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.model.community.ForwardOrigin
import javax.inject.Inject

class ForwardMessageUseCase @Inject constructor(
    private val postToForum: PostToForumUseCase,
    private val sendGroupMessage: SendGroupMessageUseCase,
) {
    suspend operator fun invoke(text: String, origin: ForwardOrigin, chat: ChatId, senderId: String) {
        when (chat) {
            ChatId.Forum -> postToForum(authorId = senderId, text = text, forwardedFrom = origin)
            is ChatId.Group -> sendGroupMessage(
                groupId = chat.id,
                senderId = senderId,
                text = text,
                forwardedFrom = origin
            )
        }
    }
}
