package com.smartcity.greenpassport.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val ScreenBackgroundLight = Color(0xFFF2F2F7)
private val CardBackgroundLight = Color(0xFFFFFFFF)
private val FieldBackgroundLight = Color(0xFFE5E5EA)
private val ForestLight = Color(0xFF1F6B47)
private val OnForestLight = Color(0xFFFFFFFF)
private val LimeLight = Color(0xFFC3EE5A)
private val OnLimeLight = Color(0xFF17331F)
private val LabelLight = Color(0xFF000000)
private val SecondaryTextLight = Color(0xFF8A8A8E)
private val SeparatorLight = Color(0xFFC6C6C8)
private val MintSurfaceLight = Color(0xFFE6F5EC)
private val MintSurfaceHighLight = Color(0xFFC9E6D3)
private val ErrorLight = Color(0xFFFF3B30)
private val OnErrorLight = Color(0xFFFFFFFF)

private val ScreenBackgroundDark = Color(0xFF000000)
private val CardBackgroundDark = Color(0xFF1C1C1E)
private val FieldBackgroundDark = Color(0xFF2C2C2E)
private val ForestDark = Color(0xFF2E8C5E)
private val OnForestDark = Color(0xFFFFFFFF)
private val LimeDark = Color(0xFFB5E04C)
private val OnLimeDark = Color(0xFF17331F)
private val LabelDark = Color(0xFFFFFFFF)
private val SecondaryTextDark = Color(0xFF98989F)
private val SeparatorDark = Color(0xFF38383A)
private val MintSurfaceDark = Color(0xFF1E2A24)
private val MintSurfaceHighDark = Color(0xFF32443A)
private val ErrorDark = Color(0xFFFF453A)
private val OnErrorDark = Color(0xFFFFFFFF)

val GreenPassportLightColorScheme = lightColorScheme(
    primary = ForestLight,
    onPrimary = OnForestLight,
    primaryContainer = MintSurfaceHighLight,
    onPrimaryContainer = ForestLight,
    secondary = LimeLight,
    onSecondary = OnLimeLight,
    secondaryContainer = MintSurfaceHighLight,
    onSecondaryContainer = ForestLight,
    tertiary = ForestLight,
    onTertiary = OnForestLight,
    background = ScreenBackgroundLight,
    onBackground = LabelLight,
    surface = CardBackgroundLight,
    onSurface = LabelLight,
    surfaceVariant = FieldBackgroundLight,
    onSurfaceVariant = SecondaryTextLight,
    surfaceContainerLowest = CardBackgroundLight,
    surfaceContainerLow = CardBackgroundLight,
    surfaceContainer = CardBackgroundLight,
    surfaceContainerHigh = MintSurfaceLight,
    surfaceContainerHighest = MintSurfaceHighLight,
    surfaceDim = FieldBackgroundLight,
    outline = SecondaryTextLight,
    outlineVariant = SeparatorLight,
    error = ErrorLight,
    onError = OnErrorLight,
)

val GreenPassportDarkColorScheme = darkColorScheme(
    primary = ForestDark,
    onPrimary = OnForestDark,
    primaryContainer = MintSurfaceHighDark,
    onPrimaryContainer = LabelDark,
    secondary = LimeDark,
    onSecondary = OnLimeDark,
    secondaryContainer = MintSurfaceHighDark,
    onSecondaryContainer = LabelDark,
    tertiary = ForestDark,
    onTertiary = OnForestDark,
    background = ScreenBackgroundDark,
    onBackground = LabelDark,
    surface = CardBackgroundDark,
    onSurface = LabelDark,
    surfaceVariant = FieldBackgroundDark,
    onSurfaceVariant = SecondaryTextDark,
    surfaceContainerLowest = CardBackgroundDark,
    surfaceContainerLow = CardBackgroundDark,
    surfaceContainer = CardBackgroundDark,
    surfaceContainerHigh = MintSurfaceDark,
    surfaceContainerHighest = MintSurfaceHighDark,
    surfaceDim = FieldBackgroundDark,
    outline = SecondaryTextDark,
    outlineVariant = SeparatorDark,
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
