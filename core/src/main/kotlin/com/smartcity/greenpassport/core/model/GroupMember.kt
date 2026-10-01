package com.smartcity.greenpassport.core.model

import com.smartcity.greenpassport.core.model.profile.AvatarStyle

data class GroupMember(
    val id: String,
    val name: String?,
    val avatar: AvatarStyle,
)
