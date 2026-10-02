package com.smartcity.greenpassport.core.model

data class LocalizedTextList(
    val fallback: List<String>,
    val translations: Map<String, List<String>> = emptyMap(),
) {
    fun resolve(languageCode: String): List<String> = translations[languageCode] ?: fallback
}
