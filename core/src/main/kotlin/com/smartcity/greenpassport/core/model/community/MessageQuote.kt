package com.smartcity.greenpassport.core.model.community

data class MessageQuote(
    val messageId: String,
    val senderName: String?,
    val text: String,
) {
    val isDeleted: Boolean
        get() = text.isEmpty()

    companion object {
        private const val MAXIMUM_TEXT_LENGTH = 200

        fun make(messageId: String, senderName: String?, text: String) =
            MessageQuote(messageId = messageId, senderName = senderName, text = text.take(MAXIMUM_TEXT_LENGTH))
    }
}
