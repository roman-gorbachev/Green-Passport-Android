package com.smartcity.greenpassport.feature.tasks.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpFilterChip
import com.smartcity.greenpassport.core.designsystem.component.GpListRow
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.MascotWidget
import com.smartcity.greenpassport.core.designsystem.component.PointsChip
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.feature.tasks.R
import com.smartcity.greenpassport.feature.tasks.presentation.state.TaskFilter
import com.smartcity.greenpassport.feature.tasks.presentation.state.TasksListUiState
import com.smartcity.greenpassport.feature.tasks.presentation.viewmodels.TasksListViewModel
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun TasksListScreen(
    onTaskSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasksListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(viewModel) {
        viewModel.refresh()
        onPauseOrDispose {}
    }

    TasksListContent(
        uiState = uiState,
        onFilterSelected = viewModel::onFilterSelected,
        onTaskSelected = onTaskSelected,
        onToggleFavorite = viewModel::onToggleFavorite,
        onRetry = viewModel::refresh,
        modifier = modifier,
    )
}

@Composable
private fun TasksListContent(
    uiState: TasksListUiState,
    onFilterSelected: (TaskFilter) -> Unit,
    onTaskSelected: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyRow(
            contentPadding = PaddingValues(
                horizontal = Dimens.ScreenHorizontalPadding,
                vertical = Dimens.SpacingSmall,
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        ) {
            if (uiState.profile != null) {
                item {
                    GpFilterChip(
                        label = stringResource(R.string.for_you),
                        selected = uiState.effectiveFilter == TaskFilter.ForYou,
                        onClick = { onFilterSelected(TaskFilter.ForYou) },
                    )
                }
            }
            item {
                GpFilterChip(
                    label = stringResource(R.string.tasks_filter_all),
                    selected = uiState.effectiveFilter == TaskFilter.All,
                    onClick = { onFilterSelected(TaskFilter.All) },
                )
            }
            items(TaskCategory.entries) { category ->
                GpFilterChip(
                    label = stringResource(taskCategoryLabelRes(category)),
                    selected = uiState.effectiveFilter == TaskFilter.Category(category),
                    onClick = { onFilterSelected(TaskFilter.Category(category)) },
                )
            }
        }

        when {
            uiState.isLoading -> LoadingContent(modifier = Modifier.fillMaxSize())
            uiState.hasError -> ErrorContent(
                message = stringResource(CoreR.string.error_generic_message),
                retryLabel = stringResource(CoreR.string.retry_button),
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
            )
            uiState.visibleTasks.isEmpty() -> EmptyContent(
                message = stringResource(R.string.tasks_empty),
                modifier = Modifier.fillMaxSize(),
            )

            else -> LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = Dimens.ScreenHorizontalPadding,
                    vertical = Dimens.SpacingSmall,
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
            ) {
                items(uiState.visibleTasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        isCompleted = uiState.completedTaskIds.contains(task.id),
                        isPending = uiState.pendingTaskIds.contains(task.id),
                        isFavorite = uiState.favoriteTaskIds.contains(task.id),
                        onClick = { onTaskSelected(task.id) },
                        onToggleFavorite = { onToggleFavorite(task.id) },
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }
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
    GpListRow(
        title = task.title,
        subtitle = when {
            isCompleted -> stringResource(R.string.task_detail_completed_label)
            isPending -> stringResource(R.string.under_review)
            else -> null
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
                            GreenPassportTheme.sectionColors.feedback
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        },
        onClick = onClick,
        modifier = modifier,
    )
}
