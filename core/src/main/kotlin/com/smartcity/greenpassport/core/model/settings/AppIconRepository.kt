package com.smartcity.greenpassport.core.model.settings

interface AppIconRepository {
    val current: AppIcon
    fun set(icon: AppIcon)
}
