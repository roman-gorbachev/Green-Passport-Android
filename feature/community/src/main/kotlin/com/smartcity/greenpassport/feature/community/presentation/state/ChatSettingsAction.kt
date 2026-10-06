package com.smartcity.greenpassport.feature.community.presentation.state

import com.smartcity.greenpassport.core.model.community.ChatSettings

fun ChatSettings.applying(action: ChatListAction): ChatSettings = when (action) {
    ChatListAction.TOGGLE_PIN -> copy(isPinned = !isPinned)
    ChatListAction.TOGGLE_MUTE -> copy(isMuted = !isMuted)
    ChatListAction.TOGGLE_ARCHIVE -> copy(isArchived = !isArchived)
}
