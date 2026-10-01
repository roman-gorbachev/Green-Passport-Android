package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

private const val TITLE_MAX_LINES = 2

@Composable
fun ListRowContent(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: @Composable () -> Unit = { ListRowChevron() },
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.ListRowHeight)
            .padding(horizontal = Dimens.CardPadding, vertical = Dimens.SpacingSmall),
    ) {
        if (leading != null) {
            leading()
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingHairline),
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = if (leading != null) Dimens.ListRowContentGap else Dimens.SpacingNone,
                    end = Dimens.ListRowContentGap,
                ),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = TITLE_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        trailing()
    }
}
