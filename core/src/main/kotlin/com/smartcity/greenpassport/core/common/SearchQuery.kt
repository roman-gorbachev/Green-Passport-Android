package com.smartcity.greenpassport.core.common

fun String.matchesSearchQuery(query: String): Boolean {
    val trimmed = query.trim()
    return trimmed.isEmpty() || contains(trimmed, ignoreCase = true)
}
