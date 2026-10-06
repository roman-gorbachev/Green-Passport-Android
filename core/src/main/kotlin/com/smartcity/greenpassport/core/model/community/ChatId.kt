package com.smartcity.greenpassport.core.model.community

sealed interface ChatId {
    val rawValue: String

    data object Forum : ChatId {
        override val rawValue = FORUM_RAW_VALUE
    }

    data class Group(val id: String) : ChatId {
        override val rawValue = id
    }

    companion object {
        private const val FORUM_RAW_VALUE = "forum"

        fun fromRawValue(rawValue: String): ChatId = if (rawValue == FORUM_RAW_VALUE) Forum else Group(rawValue)
    }
}
