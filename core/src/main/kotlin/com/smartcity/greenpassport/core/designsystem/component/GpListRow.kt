package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme

@Composable
fun GpListRow(
    title: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: @Composable () -> Unit = { ListRowChevron() },
) {
    GpSurfaceCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.ListRowHeight),
    ) {
        ListRowContent(title = title, subtitle = subtitle, leading = leading, trailing = trailing)
    }
}

@Preview
@Composable
private fun GpListRowPreview() {
    GreenPassportTheme {
        GpListRow(
            title = "Субботник",
            subtitle = "+50 оч.",
            leading = { MascotWidget(size = Dimens.ListRowMascotSize) },
            onClick = {},
        )
    }
}
