package com.smartcity.greenpassport.feature.calendar.presentation.state

import java.time.LocalDate
import java.time.YearMonth

data class CalendarSelection(
    val day: LocalDate,
    val month: YearMonth,
)
