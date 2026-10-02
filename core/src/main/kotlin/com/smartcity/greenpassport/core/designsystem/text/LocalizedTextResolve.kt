package com.smartcity.greenpassport.core.designsystem.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLocale
import com.smartcity.greenpassport.core.model.LocalizedText
import com.smartcity.greenpassport.core.model.LocalizedTextList

@Composable
fun currentLanguageCode(): String = LocalLocale.current.platformLocale.language

@Composable
fun LocalizedText.localized(): String = resolve(currentLanguageCode())

@Composable
fun LocalizedTextList.localized(): List<String> = resolve(currentLanguageCode())
