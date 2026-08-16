package com.smartcity.greenpassport.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalRewardTierColors = staticCompositionLocalOf { LightRewardTierColors }

object GreenPassportTheme {
    val rewardTierColors: RewardTierColors
        @Composable
        get() = LocalRewardTierColors.current
}

@Composable
fun GreenPassportTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) GreenPassportDarkColorScheme else GreenPassportLightColorScheme
    val rewardTierColors = if (darkTheme) DarkRewardTierColors else LightRewardTierColors

    CompositionLocalProvider(LocalRewardTierColors provides rewardTierColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = GreenPassportTypography,
            content = content,
        )
    }
}
