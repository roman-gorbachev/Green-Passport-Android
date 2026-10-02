package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

@Composable
fun ListSectionRow(
    title: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleColor: Color = Color.Unspecified,
    leading: (@Composable () -> Unit)? = null,
    trailing: @Composable () -> Unit = { ListRowChevron() },
    showDivider: Boolean = false,
) {
    Column(modifier = modifier) {
        ListRowContent(
            title = title,
            titleColor = titleColor,
            subtitle = subtitle,
            leading = leading,
            trailing = trailing,
            modifier = if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier,
        )
        if (showDivider) {
            ListSectionDivider(hasLeading = leading != null)
        }
    }
}

@Composable
fun ListSectionDivider(
    modifier: Modifier = Modifier,
    hasLeading: Boolean = true,
) {
    val inset = if (hasLeading) {
        Dimens.CardPadding + Dimens.TileSize + Dimens.ListRowContentGap
    } else {
        Dimens.CardPadding
    }
    HorizontalDivider(
        thickness = Dimens.DividerThickness,
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = modifier.padding(start = inset),
    )
}
