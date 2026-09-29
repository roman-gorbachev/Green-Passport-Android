package com.smartcity.greenpassport.feature.profile.presentation.profile

import androidx.lifecycle.ViewModel
import com.smartcity.greenpassport.core.model.settings.AppLanguage
import com.smartcity.greenpassport.feature.profile.domain.GetAppLanguageUseCase
import com.smartcity.greenpassport.feature.profile.domain.SetAppLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AppLanguageViewModel @Inject constructor(
    getAppLanguage: GetAppLanguageUseCase,
    private val setAppLanguage: SetAppLanguageUseCase,
) : ViewModel() {

    private val _language = MutableStateFlow(getAppLanguage())
    val language = _language.asStateFlow()

    fun onLanguageSelected(language: AppLanguage) {
        if (language == _language.value) return
        _language.value = language
        setAppLanguage(language)
    }
}
