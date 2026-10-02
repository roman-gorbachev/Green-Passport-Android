package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme

@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    navigationButton: @Composable () -> Unit = { onNavigateBack?.let { GpBackButton(onClick = it) } },
    actions: @Composable () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Dimens.ScreenHorizontalPadding,
                    end = Dimens.ScreenHorizontalPadding,
                    top = Dimens.SpacingExtraSmall,
                    bottom = Dimens.SpacingCompact,
                ),
        ) {
            Box(modifier = Modifier.size(Dimens.BackButtonSize)) {
                navigationButton()
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.SpacingSmall),
            )
            Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.size(Dimens.BackButtonSize)) {
                actions()
            }
        }
    }
}

@Preview
@Composable
private fun ScreenHeaderPreview() {
    GreenPassportTheme {
        ScreenHeader(title = "Задания", onNavigateBack = {})
    }
}
