package com.smartcity.greenpassport.feature.profile.presentation.profile

import androidx.annotation.StringRes
import com.smartcity.greenpassport.core.model.settings.AppLanguage
import com.smartcity.greenpassport.feature.profile.R

@StringRes
fun appLanguageLabelRes(language: AppLanguage): Int = when (language) {
    AppLanguage.SYSTEM -> R.string.system_language
    AppLanguage.RUSSIAN -> R.string.russian_language_name
    AppLanguage.ENGLISH -> R.string.english_language_name
}
