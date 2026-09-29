package com.smartcity.greenpassport.core.moderation

interface TextModerator {
    fun isAllowed(text: String): Boolean
}
