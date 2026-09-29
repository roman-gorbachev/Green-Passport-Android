package com.smartcity.greenpassport.feature.profile.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.designsystem.component.GpFilterChip
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.profile.R
import com.smartcity.greenpassport.feature.profile.presentation.bookmarks.BookmarksScreen

@Composable
fun FavoritesTabScreen(
    onTaskSelected: (String) -> Unit,
    onTipSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTips by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            modifier = Modifier.padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingSmall),
        ) {
            GpFilterChip(
                label = stringResource(R.string.tasks),
                selected = !showTips,
                onClick = { showTips = false },
            )
            GpFilterChip(
                label = stringResource(R.string.tips),
                selected = showTips,
                onClick = { showTips = true },
            )
        }
        if (showTips) {
            BookmarksScreen(onTipSelected = onTipSelected)
        } else {
            FavoritesScreen(onTaskSelected = onTaskSelected)
        }
    }
}
