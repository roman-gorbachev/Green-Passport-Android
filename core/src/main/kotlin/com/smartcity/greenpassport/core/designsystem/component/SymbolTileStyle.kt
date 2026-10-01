package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.ui.graphics.Color

sealed interface SymbolTileStyle {
    data object Accent : SymbolTileStyle

    data object Prominent : SymbolTileStyle

    data object Muted : SymbolTileStyle

    data class Tinted(val color: Color) : SymbolTileStyle
}
