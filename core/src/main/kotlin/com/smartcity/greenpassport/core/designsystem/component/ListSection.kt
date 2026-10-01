package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme

@Composable
fun ListSection(
    modifier: Modifier = Modifier,
    header: String? = null,
    footer: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (header != null) {
            Text(
                text = header,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = Dimens.CardPadding, bottom = Dimens.SpacingSmall),
            )
        }
        GpSurfaceCard(modifier = Modifier.fillMaxWidth(), content = content)
        if (footer != null) {
            Column(
                modifier = Modifier.padding(
                    start = Dimens.CardPadding,
                    end = Dimens.CardPadding,
                    top = Dimens.SpacingSmall,
                ),
            ) {
                footer()
            }
        }
    }
}

@Preview
@Composable
private fun ListSectionPreview() {
    GreenPassportTheme {
        ListSection(header = "Сообщество") {
            ListSectionRow(
                title = "Форум",
                onClick = {},
                leading = { SymbolTile(icon = Icons.Filled.Forum) },
                showDivider = true,
            )
            ListSectionRow(title = "Группы", onClick = {}, leading = { SymbolTile(icon = Icons.Filled.Groups) })
        }
    }
}
