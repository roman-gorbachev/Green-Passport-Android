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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.PillListItem
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.feature.tasks.R
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

    LaunchedEffect(Unit) { viewModel.refresh() }

    TasksListContent(
        uiState = uiState,
        onCategorySelected = viewModel::onCategorySelected,
        onTaskSelected = onTaskSelected,
        onToggleFavorite = viewModel::onToggleFavorite,
        onRetry = viewModel::refresh,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksListContent(
    uiState: TasksListUiState,
    onCategorySelected: (TaskCategory?) -> Unit,
    onTaskSelected: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyRow(
            contentPadding = PaddingValues(Dimens.SpacingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text(stringResource(R.string.tasks_filter_all)) },
                )
            }
            items(TaskCategory.entries) { category ->
                FilterChip(
                    selected = uiState.selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(stringResource(taskCategoryLabelRes(category))) },
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
                contentPadding = PaddingValues(Dimens.SpacingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            ) {
                items(uiState.visibleTasks) { task ->
                    TaskRow(
                        task = task,
                        isCompleted = uiState.completedTaskIds.contains(task.id),
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
private fun TaskRow(
    task: Task,
    isCompleted: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        PillListItem(
            title = task.title,
            leadingIcon = if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            onClick = onClick,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
