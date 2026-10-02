package com.smartcity.greenpassport.core.model

data class StreakSummary(
    val days: Int,
    val isTodayCounted: Boolean,
    val daysUntilBonus: Int,
    val week: List<StreakWeekDay>,
)
