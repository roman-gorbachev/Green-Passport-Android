package com.smartcity.greenpassport.feature.profile.presentation.achievements

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.ui.graphics.vector.ImageVector
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

fun achievementIcon(id: AchievementId): ImageVector = when (id) {
    AchievementId.FIRST_TASK -> Icons.Filled.Eco
    AchievementId.TASK_MASTER -> Icons.Filled.Verified
    AchievementId.EVENT_GOER -> Icons.Filled.EventAvailable
    AchievementId.ECO_READER -> Icons.AutoMirrored.Filled.MenuBook
    AchievementId.COMMUNITY_MEMBER -> Icons.Filled.Groups
    AchievementId.LEVEL_FIVE -> Icons.Filled.Star
}
