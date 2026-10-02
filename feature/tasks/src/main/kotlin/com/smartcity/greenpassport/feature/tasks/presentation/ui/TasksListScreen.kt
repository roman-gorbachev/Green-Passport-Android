package com.smartcity.greenpassport.feature.tasks.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.smartcity.greenpassport.core.designsystem.text.localized
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.feature.tasks.R
import com.smartcity.greenpassport.feature.tasks.presentation.state.TaskFilterChip
import com.smartcity.greenpassport.feature.tasks.presentation.state.TaskFilters
import com.smartcity.greenpassport.feature.tasks.presentation.state.TasksListUiState
import com.smartcity.greenpassport.feature.tasks.presentation.state.statusFilterLabelRes
import com.smartcity.greenpassport.feature.tasks.presentation.state.verificationLabelRes
import com.smartcity.greenpassport.feature.tasks.presentation.viewmodels.TasksListViewModel

@Composable
fun TasksListScreen(
    onTaskSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: TasksListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TasksListContent(
        uiState = uiState,
        onFiltersChanged = viewModel::onFiltersChanged,
        onTaskSelected = onTaskSelected,
        onToggleFavorite = viewModel::onToggleFavorite,
        onRetry = viewModel::retry,
        contentPadding = contentPadding,
        modifier = modifier,
    )

    if (uiState.isFilterSheetVisible) {
        TaskFiltersSheet(
            initialFilters = uiState.filters,
            profileCity = uiState.profile?.city,
            resultCount = { filters -> uiState.tasksMatching(filters).size },
            onApply = viewModel::onFiltersChanged,
            onDismiss = { viewModel.onFilterSheetVisibilityChanged(false) },
        )
    }
}

@Composable
private fun TasksListContent(
    uiState: TasksListUiState,
    onFiltersChanged: (TaskFilters) -> Unit,
    onTaskSelected: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRetry: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val listPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding())
    val chips = uiState.filters.chips()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding()),
    ) {
        if (chips.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(
                    horizontal = Dimens.ScreenHorizontalPadding,
                    vertical = Dimens.SpacingSmall
                ),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            ) {
                items(chips) { chip ->
                    ActiveFilterChip(
                        label = chipLabel(chip, uiState.profile?.city),
                        onRemove = { onFiltersChanged(chip.remaining) },
                    )
                }
            }
        }

        when {
            uiState.isLoading -> LoadingContent(modifier = Modifier.padding(listPadding))
            uiState.hasError -> ErrorContent(
                message = stringResource(CoreR.string.error_generic_message),
                retryLabel = stringResource(CoreR.string.retry_button),
                onRetry = onRetry,
                modifier = Modifier.padding(listPadding),
            )
            uiState.visibleTasks.isEmpty() -> EmptyContent(
                message = stringResource(R.string.tasks_empty),
                modifier = Modifier.padding(listPadding),
            )

            else -> LazyColumn(
                contentPadding = listPadding + PaddingValues(
                    horizontal = Dimens.ScreenHorizontalPadding,
                    vertical = Dimens.SpacingSmall,
                ),
            ) {
                listSectionItems(uiState.visibleTasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        isCompleted = uiState.completedTaskIds.contains(task.id),
                        isPending = uiState.pendingTaskIds.contains(task.id),
                        isFavorite = uiState.favoriteTaskIds.contains(task.id),
                        onClick = { onTaskSelected(task.id) },
                        onToggleFavorite = { onToggleFavorite(task.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveFilterChip(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val removeLabel = stringResource(R.string.remove_filter)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.CornerRadiusPill))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .clickable(role = Role.Button, onClickLabel = removeLabel, onClick = onRemove)
            .padding(horizontal = Dimens.SpacingCompact, vertical = Dimens.SpacingSmall),
    ) {
        Text(text = label, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.IconSizeExtraSmall),
        )
    }
}

@Composable
private fun chipLabel(chip: TaskFilterChip, profileCity: String?): String = when (chip) {
    is TaskFilterChip.Status -> stringResource(statusFilterLabelRes(chip.status))
    is TaskFilterChip.City -> cityLabel(chip.city, profileCity)
    is TaskFilterChip.Verification -> stringResource(verificationLabelRes(chip.verification))
    is TaskFilterChip.Category -> stringResource(taskCategoryLabelRes(chip.category))
}

@Composable
private fun TaskRow(
    task: Task,
    isCompleted: Boolean,
    isPending: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListRowContent(
        title = task.title.localized(),
        subtitle = when {
            isCompleted -> stringResource(R.string.task_detail_completed_label)
            isPending -> stringResource(R.string.under_review)
            else -> stringResource(taskCategoryLabelRes(task.category))
        },
        leading = { MascotWidget(size = Dimens.ListRowMascotSize) },
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isCompleted) {
                    PointsChip(points = task.rewardPoints)
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(CoreR.string.favorites),
                        tint = if (isFavorite) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        },
        modifier = modifier.clickable(role = Role.Button, onClick = onClick),
    )
}
