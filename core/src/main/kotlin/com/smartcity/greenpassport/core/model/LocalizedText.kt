package com.smartcity.greenpassport.core.model

data class LocalizedText(
    val fallback: String,
    val translations: Map<String, String> = emptyMap(),
) {
    fun resolve(languageCode: String): String = translations[languageCode] ?: fallback
}
