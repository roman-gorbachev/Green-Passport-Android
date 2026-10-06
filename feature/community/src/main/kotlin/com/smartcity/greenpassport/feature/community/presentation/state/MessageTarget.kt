package com.smartcity.greenpassport.feature.community.presentation.state

import com.smartcity.greenpassport.core.model.ForumPost
import com.smartcity.greenpassport.core.model.GroupMessage
import com.smartcity.greenpassport.core.model.community.ForwardOrigin
import com.smartcity.greenpassport.core.model.community.MessageQuote

data class MessageTarget(
    val id: String,
    val senderName: String?,
    val text: String,
    val isOwn: Boolean,
    val isDeleted: Boolean,
    val canReport: Boolean,
    val forwardedFrom: ForwardOrigin?,
) {
    val quote: MessageQuote
        get() = MessageQuote.make(messageId = id, senderName = senderName, text = text)

    val forwardOrigin: ForwardOrigin
        get() = forwardedFrom ?: ForwardOrigin(senderName)

    companion object {
        fun of(post: ForumPost, currentUserId: String?) = MessageTarget(
            id = post.id,
            senderName = post.authorName,
            text = post.text,
            isOwn = post.authorId == currentUserId,
            isDeleted = post.isDeleted,
            canReport = currentUserId != null && post.authorId != currentUserId,
            forwardedFrom = post.forwardedFrom,
        )

        fun of(message: GroupMessage, currentUserId: String?) = MessageTarget(
            id = message.id,
            senderName = message.senderName,
            text = message.text,
            isOwn = message.senderId == currentUserId,
            isDeleted = message.isDeleted,
            canReport = false,
            forwardedFrom = message.forwardedFrom,
        )
    }
}
