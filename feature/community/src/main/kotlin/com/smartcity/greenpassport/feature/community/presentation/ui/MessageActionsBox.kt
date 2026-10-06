package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.smartcity.greenpassport.core.model.moderation.ReportReason
import com.smartcity.greenpassport.feature.community.R
import com.smartcity.greenpassport.feature.community.presentation.state.MessageAction
import com.smartcity.greenpassport.feature.community.presentation.state.MessageTarget

@Composable
fun MessageActionsBox(
    target: MessageTarget,
    onAction: (MessageAction, MessageTarget) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = modifier.combinedClickable(
            enabled = !target.isDeleted,
            onClick = {},
            onLongClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                isExpanded = true
            },
        ),
    ) {
        content()
        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            val select = { action: MessageAction ->
                isExpanded = false
                onAction(action, target)
            }
            ActionItem(stringResource(R.string.reply), Icons.AutoMirrored.Filled.Reply) { select(MessageAction.Reply) }
            ActionItem(stringResource(R.string.copy), Icons.Filled.ContentCopy) { select(MessageAction.Copy) }
            ActionItem(
                stringResource(R.string.forward),
                Icons.AutoMirrored.Filled.Forward
            ) { select(MessageAction.Forward) }
            if (target.isOwn) {
                ActionItem(stringResource(R.string.edit), Icons.Filled.Edit) { select(MessageAction.Edit) }
                ActionItem(stringResource(R.string.delete), Icons.Filled.Delete, isDestructive = true) {
                    select(MessageAction.Delete)
                }
            }
            if (target.canReport) {
                HorizontalDivider()
                ReportReason.entries.forEach { reason ->
                    ActionItem(stringResource(reportReasonLabelRes(reason)), Icons.Outlined.Flag) {
                        select(MessageAction.Report(reason))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionItem(
    title: String,
    icon: ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit,
) {
    val color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    DropdownMenuItem(
        text = { Text(text = title, color = color) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = color) },
        onClick = onClick,
    )
}
