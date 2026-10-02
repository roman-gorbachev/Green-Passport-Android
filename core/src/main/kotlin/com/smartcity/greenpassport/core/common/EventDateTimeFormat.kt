package com.smartcity.greenpassport.core.common

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val EVENT_DATE_PATTERN = "d MMMM"
private const val EVENT_TIME_PATTERN = "HH:mm"

fun formatEventDate(epochMillis: Long, locale: Locale): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(EVENT_DATE_PATTERN, locale))

fun formatEventTime(epochMillis: Long, locale: Locale): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(EVENT_TIME_PATTERN, locale))

fun eventDay(epochMillis: Long): LocalDate =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
