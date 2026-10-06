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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.designsystem.component.GpDropdownMenu
import com.smartcity.greenpassport.core.designsystem.component.GpDropdownMenuItem
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
        GpDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            GpDropdownMenuItem(
                text = stringResource(if (isMuted) R.string.unmute_chat else R.string.mute_chat),
                icon = if (isMuted) Icons.Filled.Notifications else Icons.Filled.NotificationsOff,
                onClick = {
                    isExpanded = false
                    onToggleMute()
                },
            )
            GpDropdownMenuItem(
                text = stringResource(R.string.group_members),
                icon = Icons.Filled.People,
                onClick = {
                    isExpanded = false
                    onShowMembers()
                },
            )
            if (inviteText != null) {
                GpDropdownMenuItem(
                    text = inviteTitle,
                    icon = Icons.Filled.PersonAdd,
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
            GpDropdownMenuItem(
                text = stringResource(R.string.leave_group),
                icon = Icons.AutoMirrored.Filled.Logout,
                onClick = {
                    isExpanded = false
                    onLeave()
                },
                isDestructive = true,
            )
        }
    }
}
