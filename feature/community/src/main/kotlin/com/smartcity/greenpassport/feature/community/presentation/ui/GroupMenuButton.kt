package com.smartcity.greenpassport.feature.community.presentation.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.feature.community.R

@Composable
fun GroupMenuButton(
    group: CommunityGroup,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onShowMembers: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val inviteText = group.inviteCode?.let { code -> stringResource(R.string.group_invite_share_msg, group.name, code) }
    val inviteTitle = stringResource(R.string.invite)
    Box(modifier = modifier) {
        IconButton(onClick = { isExpanded = true }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.more),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(if (isMuted) R.string.unmute_chat else R.string.mute_chat)) },
                leadingIcon = {
                    Icon(
                        imageVector = if (isMuted) Icons.Filled.Notifications else Icons.Filled.NotificationsOff,
                        contentDescription = null,
                    )
                },
                onClick = {
                    isExpanded = false
                    onToggleMute()
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.group_members)) },
                leadingIcon = { Icon(imageVector = Icons.Filled.People, contentDescription = null) },
                onClick = {
                    isExpanded = false
                    onShowMembers()
                },
            )
            if (inviteText != null) {
                DropdownMenuItem(
                    text = { Text(inviteTitle) },
                    leadingIcon = { Icon(imageVector = Icons.Filled.PersonAdd, contentDescription = null) },
                    onClick = {
                        isExpanded = false
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, inviteText)
                        }
                        context.startActivity(Intent.createChooser(intent, inviteTitle))
                    },
                )
            }
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.leave_group), color = MaterialTheme.colorScheme.error) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                },
                onClick = {
                    isExpanded = false
                    onLeave()
                },
            )
        }
    }
}
