package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.model.community.ChatSummary
import com.smartcity.greenpassport.feature.community.R
import com.smartcity.greenpassport.feature.community.presentation.state.ChatListAction

@Composable
fun ChatListRow(
    chat: ChatSummary,
    onOpen: () -> Unit,
    onAction: (ChatListAction) -> Unit,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val settings = chat.settings
    Box(
        modifier = modifier.combinedClickable(
            onClick = onOpen,
            onLongClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                isExpanded = true
            },
        ),
    ) {
        ChatRow(
            chatId = chat.chatId,
            title = chat.groupName ?: stringResource(R.string.community_forum_title),
            onClick = null,
            lastMessageAtEpochMillis = chat.lastMessageAtEpochMillis,
            isPinned = settings.isPinned,
            isMuted = settings.isMuted,
            showDivider = showDivider,
        )
        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            val select = { action: ChatListAction ->
                isExpanded = false
                onAction(action)
            }
            MenuItem(
                title = stringResource(if (settings.isPinned) R.string.unpin_chat else R.string.pin_chat),
                icon = if (settings.isPinned) Icons.Outlined.PushPin else Icons.Filled.PushPin,
            ) { select(ChatListAction.TOGGLE_PIN) }
            MenuItem(
                title = stringResource(if (settings.isMuted) R.string.unmute_chat else R.string.mute_chat),
                icon = if (settings.isMuted) Icons.Filled.Notifications else Icons.Filled.NotificationsOff,
            ) { select(ChatListAction.TOGGLE_MUTE) }
            MenuItem(
                title = stringResource(if (settings.isArchived) R.string.unarchive_chat else R.string.archive_chat),
                icon = if (settings.isArchived) Icons.Filled.Unarchive else Icons.Filled.Archive,
            ) { select(ChatListAction.TOGGLE_ARCHIVE) }
        }
    }
}

@Composable
private fun MenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(title) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        onClick = onClick,
    )
}
