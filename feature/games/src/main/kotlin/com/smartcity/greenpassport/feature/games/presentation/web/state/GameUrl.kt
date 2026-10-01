package com.smartcity.greenpassport.feature.games.presentation.web.state

import android.net.Uri
import com.smartcity.greenpassport.core.model.games.Game
import com.smartcity.greenpassport.feature.games.BuildConfig

private const val LANGUAGE_PARAMETER = "lang"
private const val THEME_PARAMETER = "theme"
private const val THEME_DARK = "dark"
private const val THEME_LIGHT = "light"
private val supportedLanguages = setOf("ru", "be", "en")
private const val DEFAULT_LANGUAGE = "ru"

fun gameUrl(game: Game, language: String, isDark: Boolean): String =
    Uri.parse(BuildConfig.GAMES_BASE_URL).buildUpon()
        .appendEncodedPath(game.path)
        .appendQueryParameter(LANGUAGE_PARAMETER, language.takeIf { it in supportedLanguages } ?: DEFAULT_LANGUAGE)
        .appendQueryParameter(THEME_PARAMETER, if (isDark) THEME_DARK else THEME_LIGHT)
        .build()
        .toString()
