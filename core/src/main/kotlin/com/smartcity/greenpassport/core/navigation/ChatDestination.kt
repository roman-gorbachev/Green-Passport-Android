package com.smartcity.greenpassport.core.navigation

import com.smartcity.greenpassport.core.model.community.ChatId

val ChatId.destination: Destination
    get() = when (this) {
        ChatId.Forum -> Destination.Forum
        is ChatId.Group -> Destination.CommunityGroup(id)
    }
