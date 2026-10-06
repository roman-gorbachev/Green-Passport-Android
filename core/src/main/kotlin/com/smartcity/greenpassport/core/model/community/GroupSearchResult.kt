package com.smartcity.greenpassport.core.model.community

import com.smartcity.greenpassport.core.model.CommunityGroup

data class GroupSearchResult(
    val group: CommunityGroup,
    val matchedMemberName: String?,
)
