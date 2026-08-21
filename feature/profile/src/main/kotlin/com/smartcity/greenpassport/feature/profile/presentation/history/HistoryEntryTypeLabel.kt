package com.smartcity.greenpassport.feature.profile.presentation.history

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.core.model.HistoryEntryType
import com.smartcity.greenpassport.feature.profile.R

fun historyEntryTypeLabelRes(type: HistoryEntryType): Int = when (type) {
    HistoryEntryType.TASK_COMPLETED -> R.string.history_type_task_completed
    HistoryEntryType.EVENT_ATTENDED -> R.string.history_type_event_attended
    HistoryEntryType.REWARD_REDEEMED -> R.string.history_type_reward_redeemed
}

fun historyEntryTypeIcon(type: HistoryEntryType): ImageVector = when (type) {
    HistoryEntryType.TASK_COMPLETED -> Icons.Filled.TaskAlt
    HistoryEntryType.EVENT_ATTENDED -> Icons.Filled.Event
    HistoryEntryType.REWARD_REDEEMED -> Icons.Filled.CardGiftcard
}
