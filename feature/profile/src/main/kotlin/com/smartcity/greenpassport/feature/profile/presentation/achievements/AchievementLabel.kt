package com.smartcity.greenpassport.feature.profile.presentation.achievements

import com.smartcity.greenpassport.core.model.AchievementId
import com.smartcity.greenpassport.feature.profile.R

fun achievementTitleRes(id: AchievementId): Int = when (id) {
    AchievementId.FIRST_TASK -> R.string.achievement_first_task_title
    AchievementId.TASK_MASTER -> R.string.achievement_task_master_title
    AchievementId.EVENT_GOER -> R.string.achievement_event_goer_title
    AchievementId.ECO_READER -> R.string.achievement_eco_reader_title
    AchievementId.COMMUNITY_MEMBER -> R.string.achievement_community_member_title
    AchievementId.LEVEL_FIVE -> R.string.achievement_level_five_title
}

fun achievementDescriptionRes(id: AchievementId): Int = when (id) {
    AchievementId.FIRST_TASK -> R.string.achievement_first_task_description
    AchievementId.TASK_MASTER -> R.string.achievement_task_master_description
    AchievementId.EVENT_GOER -> R.string.achievement_event_goer_description
    AchievementId.ECO_READER -> R.string.achievement_eco_reader_description
    AchievementId.COMMUNITY_MEMBER -> R.string.achievement_community_member_description
    AchievementId.LEVEL_FIVE -> R.string.achievement_level_five_description
}
