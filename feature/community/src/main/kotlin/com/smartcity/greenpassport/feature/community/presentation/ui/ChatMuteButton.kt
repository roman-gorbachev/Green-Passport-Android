package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.feature.community.R

@Composable
fun ChatMuteButton(
    isMuted: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onToggle, modifier = modifier) {
        Icon(
            imageVector = if (isMuted) Icons.Filled.NotificationsOff else Icons.Filled.Notifications,
            contentDescription = stringResource(if (isMuted) R.string.unmute_chat else R.string.mute_chat),
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}
