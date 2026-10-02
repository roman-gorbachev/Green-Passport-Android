package com.smartcity.greenpassport.feature.calendar.presentation.state

import com.smartcity.greenpassport.core.common.eventDay
import com.smartcity.greenpassport.core.model.EcoEvent
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val events: List<EcoEvent> = emptyList(),
    val selectedDay: LocalDate = LocalDate.now(),
    val visibleMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
) {
    val eventCounts: Map<LocalDate, Int>
        get() = events.groupingBy { eventDay(it.startAtEpochMillis) }.eachCount()

    val dayEvents: List<EcoEvent>
        get() = events.filter { eventDay(it.startAtEpochMillis) == selectedDay }
}
