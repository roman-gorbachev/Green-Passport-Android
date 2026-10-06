package com.smartcity.greenpassport.feature.profile.presentation.profile

import androidx.lifecycle.ViewModel
import com.smartcity.greenpassport.core.model.settings.AppIcon
import com.smartcity.greenpassport.feature.profile.domain.GetAppIconUseCase
import com.smartcity.greenpassport.feature.profile.domain.SetAppIconUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AppIconViewModel @Inject constructor(
    getAppIcon: GetAppIconUseCase,
    private val setAppIcon: SetAppIconUseCase,
) : ViewModel() {

    private val _icon = MutableStateFlow(getAppIcon())
    val icon = _icon.asStateFlow()

    fun onIconSelected(icon: AppIcon) {
        if (icon == _icon.value) return
        _icon.value = icon
        setAppIcon(icon)
    }
}
