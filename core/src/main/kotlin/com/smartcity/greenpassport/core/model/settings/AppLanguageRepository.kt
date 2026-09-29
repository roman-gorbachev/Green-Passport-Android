package com.smartcity.greenpassport.core.model.settings

interface AppLanguageRepository {
    fun getLanguage(): AppLanguage
    fun setLanguage(language: AppLanguage)
}
