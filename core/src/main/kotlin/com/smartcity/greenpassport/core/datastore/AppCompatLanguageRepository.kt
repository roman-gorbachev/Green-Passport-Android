package com.smartcity.greenpassport.core.datastore

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.smartcity.greenpassport.core.model.settings.AppLanguage
import com.smartcity.greenpassport.core.model.settings.AppLanguageRepository
import javax.inject.Inject

class AppCompatLanguageRepository @Inject constructor() : AppLanguageRepository {

    override fun getLanguage(): AppLanguage {
        val tag = AppCompatDelegate.getApplicationLocales()[0]?.language
        return AppLanguage.entries.firstOrNull { it.languageTag != null && it.languageTag == tag }
            ?: AppLanguage.SYSTEM
    }

    override fun setLanguage(language: AppLanguage) {
        val locales = language.languageTag
            ?.let(LocaleListCompat::forLanguageTags)
            ?: LocaleListCompat.getEmptyLocaleList()
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
