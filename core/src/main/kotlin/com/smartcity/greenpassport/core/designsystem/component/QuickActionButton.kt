package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme

private const val QUICK_ACTION_ICON_FRACTION = 0.5f

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.CornerRadiusMedium))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = Dimens.SpacingExtraSmall),
    ) {
        SymbolTile(icon = icon, size = Dimens.TileSizeLarge, iconFraction = QUICK_ACTION_ICON_FRACTION)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                hyphens = Hyphens.Auto,
                lineBreak = LineBreak.Paragraph,
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
private fun QuickActionButtonPreview() {
    GreenPassportTheme {
        QuickActionButton(label = "Игры", icon = Icons.Filled.SportsEsports, onClick = {})
    }
}
