package com.smartcity.greenpassport.core.model

import java.time.LocalDate

data class StreakWeekDay(
    val date: LocalDate,
    val isActive: Boolean,
    val isToday: Boolean,
)
