package com.smartcity.greenpassport.feature.games.presentation.hub.state

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.smartcity.greenpassport.core.model.games.Game
import com.smartcity.greenpassport.feature.games.BuildConfig

private const val GRADIENT_COLOR_COUNT = 2

fun gameIconUrl(game: Game): String? = game.iconPath?.let { path ->
    Uri.parse(BuildConfig.GAMES_BASE_URL).buildUpon().appendEncodedPath(path).build().toString()
}

fun gameGradient(game: Game): List<Color>? =
    game.iconColors
        .mapNotNull { hex -> runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrNull() }
        .takeIf { it.size >= GRADIENT_COLOR_COUNT }
