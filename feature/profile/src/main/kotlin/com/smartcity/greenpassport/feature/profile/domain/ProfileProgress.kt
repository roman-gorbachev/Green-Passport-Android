package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.Level

data class ProfileProgress(
    val points: Int,
    val level: Level,
)
