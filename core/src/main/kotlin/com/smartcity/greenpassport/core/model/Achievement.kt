package com.smartcity.greenpassport.core.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconUrl: String?,
    val unlockedAtEpochMillis: Long?,
)
