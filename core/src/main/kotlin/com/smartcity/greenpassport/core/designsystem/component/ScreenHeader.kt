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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    heightOffsetState: TopAppBarState? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Layout(
            content = { ScreenHeaderRow(title = title, onNavigateBack = onNavigateBack) },
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds(),
        ) { measurables, constraints ->
            val placeable = measurables.single().measure(
                constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity),
            )
            val offsetLimit = -placeable.height.toFloat()
            if (heightOffsetState != null && heightOffsetState.heightOffsetLimit != offsetLimit) {
                heightOffsetState.heightOffsetLimit = offsetLimit
            }
            val offset = heightOffsetState?.heightOffset?.roundToInt() ?: 0
            val height = (placeable.height + offset).coerceAtLeast(0)
            layout(placeable.width, height) {
                placeable.place(x = 0, y = offset)
            }
        }
    }
}

@Composable
private fun ScreenHeaderRow(
    title: String,
    onNavigateBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingSmall),
    ) {
        Box(modifier = Modifier.size(Dimens.BackButtonSize)) {
            if (onNavigateBack != null) {
                GpBackButton(onClick = onNavigateBack)
            }
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
        Spacer(modifier = Modifier.size(Dimens.BackButtonSize))
    }
}

@Preview
@Composable
private fun ScreenHeaderPreview() {
    GreenPassportTheme {
        ScreenHeader(title = "Задания", onNavigateBack = {})
    }
}
