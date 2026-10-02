package com.smartcity.greenpassport.core.model.games

data class Game(
    val id: String,
    val titles: Map<String, String>,
    val path: String,
    val materialIcon: String?,
    val iconPath: String? = null,
    val iconEmoji: String? = null,
    val iconColors: List<String> = emptyList(),
    val maxPoints: Int,
    val order: Int,
) {
    fun title(languageCode: String): String =
        titles[languageCode] ?: titles[DEFAULT_LANGUAGE] ?: titles.values.firstOrNull().orEmpty()

    companion object {
        private const val DEFAULT_LANGUAGE = "ru"
    }
}
