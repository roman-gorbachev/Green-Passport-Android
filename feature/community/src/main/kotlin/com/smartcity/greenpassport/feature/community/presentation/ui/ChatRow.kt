package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.designsystem.component.ListSectionRow
import com.smartcity.greenpassport.core.designsystem.component.LoadingLabel
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.feature.community.R
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun ChatRow(
    chatId: ChatId,
    title: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    lastMessageAtEpochMillis: Long? = null,
    subtitle: String? = null,
    isPinned: Boolean = false,
    isMuted: Boolean = false,
    isLoading: Boolean = false,
    showDivider: Boolean = false,
) {
    ListSectionRow(
        title = title,
        subtitle = subtitle ?: lastMessageAtEpochMillis?.let(::relativeTime),
        leading = { SymbolTile(icon = chatId.icon) },
        trailing = {
            LoadingLabel(isLoading = isLoading, color = MaterialTheme.colorScheme.primary) {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall)) {
                    if (isMuted) StatusIcon(Icons.Filled.NotificationsOff, stringResource(R.string.mute_chat))
                    if (isPinned) StatusIcon(Icons.Filled.PushPin, stringResource(R.string.pin_chat))
                }
            }
        },
        onClick = onClick,
        showDivider = showDivider,
        modifier = modifier,
    )
}

@Composable
private fun StatusIcon(icon: ImageVector, description: String) {
    Icon(
        imageVector = icon,
        contentDescription = description,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(Dimens.QuoteIconSize),
    )
}

private val ChatId.icon: ImageVector
    get() = when (this) {
        ChatId.Forum -> Icons.Filled.Forum
        is ChatId.Group -> Icons.Filled.Groups
    }

private fun relativeTime(epochMillis: Long): String {
    val date = Date(epochMillis)
    val today = Calendar.getInstance()
    val day = Calendar.getInstance().apply { time = date }
    val isToday = today.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
        today.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)
    return if (isToday) {
        DateFormat.getTimeInstance(DateFormat.SHORT).format(date)
    } else {
        DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
    }
}
