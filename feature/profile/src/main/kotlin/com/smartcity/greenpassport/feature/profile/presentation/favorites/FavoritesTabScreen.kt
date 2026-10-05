package com.smartcity.greenpassport.feature.profile.presentation.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.designsystem.component.SegmentedControl
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.profile.R
import com.smartcity.greenpassport.feature.profile.presentation.bookmarks.BookmarksScreen
import com.smartcity.greenpassport.feature.profile.presentation.places.SavedPlacesScreen

private const val TIPS_SEGMENT = 1
private const val PLACES_SEGMENT = 2

@Composable
fun FavoritesTabScreen(
    onTaskSelected: (String) -> Unit,
    onTipSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    var selectedSegment by rememberSaveable { mutableIntStateOf(0) }
    val listPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding()),
    ) {
        SegmentedControl(
            options = listOf(
                stringResource(R.string.tasks),
                stringResource(R.string.tips),
                stringResource(R.string.places)
            ),
            selectedIndex = selectedSegment,
            onSelect = { selectedSegment = it },
            modifier = Modifier.padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingSmall),
        )
        when (selectedSegment) {
            TIPS_SEGMENT -> BookmarksScreen(onTipSelected = onTipSelected, contentPadding = listPadding)
            PLACES_SEGMENT -> SavedPlacesScreen(contentPadding = listPadding)
            else -> FavoritesScreen(onTaskSelected = onTaskSelected, contentPadding = listPadding)
        }
    }
}
