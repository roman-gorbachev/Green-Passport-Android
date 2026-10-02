package com.smartcity.greenpassport.feature.profile.presentation.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.R as CoreR
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.ListRowContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.MascotWidget
import com.smartcity.greenpassport.core.designsystem.component.PointsChip
import com.smartcity.greenpassport.core.designsystem.component.listSectionItems
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.text.cityName
import com.smartcity.greenpassport.core.designsystem.text.localized
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.profile.R

@Composable
fun FavoritesScreen(
    onTaskSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> LoadingContent(modifier = modifier.padding(contentPadding))
        uiState.hasError -> ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::retry,
            modifier = modifier.padding(contentPadding),
        )
        uiState.tasks.isEmpty() -> EmptyContent(
            message = stringResource(R.string.favorites_empty),
            modifier = modifier.padding(contentPadding),
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding + PaddingValues(
                horizontal = Dimens.ScreenHorizontalPadding,
                vertical = Dimens.SpacingMedium,
            ),
        ) {
            listSectionItems(uiState.tasks, key = { it.id }) { task ->
                ListRowContent(
                    title = task.title.localized(),
                    subtitle = cityName(task.city),
                    leading = { MascotWidget(size = Dimens.ListRowMascotSize) },
                    trailing = { PointsChip(points = task.rewardPoints) },
                    modifier = Modifier.clickable(role = Role.Button) { onTaskSelected(task.id) },
                )
            }
        }
    }
}
