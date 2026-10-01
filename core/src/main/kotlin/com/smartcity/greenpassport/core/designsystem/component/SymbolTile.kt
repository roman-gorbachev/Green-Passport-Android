package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme

private const val CORNER_FRACTION = 0.28f
const val SYMBOL_TILE_DEFAULT_ICON_FRACTION = 0.5f

@Composable
fun SymbolTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    style: SymbolTileStyle = SymbolTileStyle.Accent,
    size: Dp = Dimens.TileSize,
    iconFraction: Float = SYMBOL_TILE_DEFAULT_ICON_FRACTION,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * CORNER_FRACTION))
            .background(style.background()),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = style.foreground(),
            modifier = Modifier.size(size * iconFraction),
        )
    }
}

@Composable
private fun SymbolTileStyle.background(): Color = when (this) {
    SymbolTileStyle.Accent -> MaterialTheme.colorScheme.surfaceContainerHighest
    SymbolTileStyle.Prominent -> MaterialTheme.colorScheme.primary
    SymbolTileStyle.Muted -> MaterialTheme.colorScheme.surfaceVariant
    is SymbolTileStyle.Tinted -> color
}

@Composable
private fun SymbolTileStyle.foreground(): Color = when (this) {
    SymbolTileStyle.Accent -> MaterialTheme.colorScheme.primary
    SymbolTileStyle.Prominent -> MaterialTheme.colorScheme.onPrimary
    SymbolTileStyle.Muted -> MaterialTheme.colorScheme.onSurfaceVariant
    is SymbolTileStyle.Tinted -> Color.White
}

@Preview
@Composable
private fun SymbolTilePreview() {
    GreenPassportTheme {
        Row {
            SymbolTile(icon = Icons.Filled.SportsEsports)
            SymbolTile(icon = Icons.Filled.SportsEsports, style = SymbolTileStyle.Prominent)
            SymbolTile(icon = Icons.Filled.SportsEsports, style = SymbolTileStyle.Muted)
            SymbolTile(
                icon = Icons.Filled.SportsEsports,
                style = SymbolTileStyle.Tinted(GreenPassportTheme.sectionColors.games),
            )
        }
    }
}
