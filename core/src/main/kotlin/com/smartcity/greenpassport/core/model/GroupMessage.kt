package com.smartcity.greenpassport.core.model

import com.smartcity.greenpassport.core.model.profile.AvatarStyle

data class GroupMessage(
    val id: String,
    val senderId: String,
    val senderName: String?,
    val senderAvatar: AvatarStyle?,
    val text: String,
    val sentAtEpochMillis: Long,
)
