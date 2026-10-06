package com.smartcity.greenpassport.core.model.community

import com.smartcity.greenpassport.core.model.CommunityGroup

object GroupSearch {
    fun matches(
        groups: List<CommunityGroup>,
        query: String,
        memberNames: Map<String, String>
    ): List<GroupSearchResult> {
        val needle = query.trim().lowercase()
        if (needle.isEmpty()) return emptyList()
        return groups.mapNotNull { group ->
            if (group.name.lowercase().contains(needle)) {
                GroupSearchResult(group = group, matchedMemberName = null)
            } else {
                group.memberIds
                    .mapNotNull { memberNames[it] }
                    .firstOrNull { it.lowercase().contains(needle) }
                    ?.let { GroupSearchResult(group = group, matchedMemberName = it) }
            }
        }
    }
}
