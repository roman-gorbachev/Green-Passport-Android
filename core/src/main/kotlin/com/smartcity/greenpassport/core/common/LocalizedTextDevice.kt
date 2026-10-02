package com.smartcity.greenpassport.core.common

import com.smartcity.greenpassport.core.model.LocalizedText
import java.util.Locale

fun LocalizedText.resolveForDeviceLanguage(): String = resolve(Locale.getDefault().language)

fun LocalizedText.matches(query: String): Boolean =
    fallback.contains(query, ignoreCase = true) || translations.values.any { it.contains(query, ignoreCase = true) }
