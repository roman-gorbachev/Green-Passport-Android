package com.smartcity.greenpassport.core.model.profile

import com.smartcity.greenpassport.core.model.TaskCategory

data class UserProfile(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val city: String,
    val interests: Set<TaskCategory>,
    val avatar: AvatarStyle,
)
