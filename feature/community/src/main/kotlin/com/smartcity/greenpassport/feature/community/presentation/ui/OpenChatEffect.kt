package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.smartcity.greenpassport.core.messaging.helpers.OpenChatTracker
import com.smartcity.greenpassport.core.model.community.ChatId

@Composable
fun OpenChatEffect(chatId: ChatId) {
    LifecycleResumeEffect(chatId) {
        OpenChatTracker.openChatId = chatId.rawValue
        onPauseOrDispose {
            if (OpenChatTracker.openChatId == chatId.rawValue) OpenChatTracker.openChatId = null
        }
    }
}
