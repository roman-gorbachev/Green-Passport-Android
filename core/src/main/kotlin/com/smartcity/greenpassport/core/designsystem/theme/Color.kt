package com.smartcity.greenpassport.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val WhiteBackgroundLight = Color(0xFFFFFFFF)
private val MintCardLight = Color(0xFFE6F5EC)
private val MintCardLowLight = Color(0xFFF1FAF4)
private val ForestLight = Color(0xFF1F6B47)
private val OnForestLight = Color(0xFFFFFFFF)
private val LimeLight = Color(0xFFC3EE5A)
private val OnLimeLight = Color(0xFF17331F)
private val InkLight = Color(0xFF0B0F10)
private val MossLight = Color(0xFF385247)
private val MossSecondaryLight = Color(0xFF6A7F75)
private val MintSurfaceLight = Color(0xFFD6F1E0)
private val MintSurfaceHighLight = Color(0xFFC9E6D3)
private val TrackLight = Color(0xFFC5DCD2)
private val PebbleLight = Color(0xFFD1DCD6)
private val ErrorLight = Color(0xFFB3261E)
private val OnErrorLight = Color(0xFFFFFFFF)

private val MintBackgroundDark = Color(0xFF121815)
private val CardDark = Color(0xFF1E2A24)
private val ForestDark = Color(0xFF2E8C5E)
private val OnForestDark = Color(0xFFFFFFFF)
private val LimeDark = Color(0xFFB5E04C)
private val OnLimeDark = Color(0xFF17331F)
private val InkDark = Color(0xFFE4EEE7)
private val MossDark = Color(0xFFA9C2B7)
private val MossSecondaryDark = Color(0xFF8AA398)
private val MintSurfaceDark = Color(0xFF28372F)
private val MintSurfaceHighDark = Color(0xFF32443A)
private val TrackDark = Color(0xFF34443B)
private val PebbleDark = Color(0xFF3A4540)
private val ErrorDark = Color(0xFFF2B8B5)
private val OnErrorDark = Color(0xFF601410)

val GreenPassportLightColorScheme = lightColorScheme(
    primary = ForestLight,
    onPrimary = OnForestLight,
    primaryContainer = MintSurfaceLight,
    onPrimaryContainer = ForestLight,
    secondary = LimeLight,
    onSecondary = OnLimeLight,
    secondaryContainer = MintSurfaceLight,
    onSecondaryContainer = MossLight,
    tertiary = ForestLight,
    onTertiary = OnForestLight,
    background = WhiteBackgroundLight,
    onBackground = InkLight,
    surface = WhiteBackgroundLight,
    onSurface = InkLight,
    surfaceVariant = MintCardLight,
    onSurfaceVariant = MossLight,
    surfaceContainerLowest = WhiteBackgroundLight,
    surfaceContainerLow = MintCardLowLight,
    surfaceContainer = MintCardLight,
    surfaceContainerHigh = MintSurfaceLight,
    surfaceContainerHighest = MintSurfaceHighLight,
    outline = MossSecondaryLight,
    surfaceDim = PebbleLight,
    outlineVariant = TrackLight,
    error = ErrorLight,
    onError = OnErrorLight,
)

val GreenPassportDarkColorScheme = darkColorScheme(
    primary = ForestDark,
    onPrimary = OnForestDark,
    primaryContainer = MintSurfaceDark,
    onPrimaryContainer = InkDark,
    secondary = LimeDark,
    onSecondary = OnLimeDark,
    secondaryContainer = MintSurfaceDark,
    onSecondaryContainer = MossDark,
    tertiary = ForestDark,
    onTertiary = OnForestDark,
    background = MintBackgroundDark,
    onBackground = InkDark,
    surface = MintBackgroundDark,
    onSurface = InkDark,
    surfaceVariant = MintSurfaceDark,
    onSurfaceVariant = MossDark,
    surfaceContainerLowest = CardDark,
    surfaceContainerLow = CardDark,
    surfaceContainer = CardDark,
    surfaceContainerHigh = MintSurfaceDark,
    surfaceContainerHighest = MintSurfaceHighDark,
    outline = MossSecondaryDark,
    surfaceDim = PebbleDark,
    outlineVariant = TrackDark,
    error = ErrorDark,
    onError = OnErrorDark,
)

val LightRewardTierColors = RewardTierColors(
    teal = Color(0xFF2A9DA6),
    coral = Color(0xFFE07A5F),
    green = Color(0xFF6FA954),
)

val DarkRewardTierColors = RewardTierColors(
    teal = Color(0xFF63B7BE),
    coral = Color(0xFFE8A088),
    green = Color(0xFF9CCB84),
)

val LightSectionColors = SectionColors(
    community = Color(0xFF34C77B),
    games = Color(0xFF8E7CF0),
    tips = Color(0xFFFF9F43),
    calendar = Color(0xFF4DA3FF),
    feedback = Color(0xFFFF6B8A),
)

val DarkSectionColors = SectionColors(
    community = Color(0xFF2FB46F),
    games = Color(0xFF7F6EE0),
    tips = Color(0xFFF08F33),
    calendar = Color(0xFF3F93EE),
    feedback = Color(0xFFEE5C7B),
)
