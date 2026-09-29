package com.smartcity.greenpassport.feature.home.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.common.formatEventDate
import com.smartcity.greenpassport.core.common.formatEventTime
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpListRow
import com.smartcity.greenpassport.core.designsystem.component.HeroImageCard
import com.smartcity.greenpassport.core.designsystem.component.LevelProgressCard
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.MascotWidget
import com.smartcity.greenpassport.core.designsystem.component.QuickActionTile
import com.smartcity.greenpassport.core.designsystem.component.SectionHeader
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.navigation.Destination
import com.smartcity.greenpassport.feature.home.R
import com.smartcity.greenpassport.feature.home.presentation.state.HomeQuickAction
import com.smartcity.greenpassport.feature.home.presentation.state.HomeUiState
import com.smartcity.greenpassport.feature.home.presentation.viewmodels.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.smartcity.greenpassport.core.R as CoreR

private const val TODAY_PATTERN = "EEEE, d MMM"
private const val QUICK_ACTIONS_VISIBLE = 3

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit,
    onEventSelected: (String) -> Unit,
    onTaskSelected: (String) -> Unit,
    onAllTasksClick: () -> Unit,
    onDestinationSelected: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(viewModel) {
        viewModel.refresh()
        onPauseOrDispose {}
    }

    HomeContent(
        uiState = uiState,
        onProfileClick = onProfileClick,
        onEventSelected = onEventSelected,
        onTaskSelected = onTaskSelected,
        onAllTasksClick = onAllTasksClick,
        onDestinationSelected = onDestinationSelected,
        onRetry = viewModel::refresh,
        modifier = modifier,
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onProfileClick: () -> Unit,
    onEventSelected: (String) -> Unit,
    onTaskSelected: (String) -> Unit,
    onAllTasksClick: () -> Unit,
    onDestinationSelected: (Destination) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoading) {
        LoadingContent(modifier = modifier)
        return
    }

    val systemBars = WindowInsets.systemBars.asPaddingValues()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Dimens.ScreenHorizontalPadding,
            end = Dimens.ScreenHorizontalPadding,
            top = systemBars.calculateTopPadding() + Dimens.SpacingLarge,
            bottom = systemBars.calculateBottomPadding() + Dimens.BottomBarReservedHeight + Dimens.SpacingMedium,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
    ) {
        item {
            HomeHeader(displayName = uiState.displayName, onProfileClick = onProfileClick)
        }
        uiState.level?.let { level ->
            item {
                LevelProgressCard(
                    level = level.number,
                    currentXp = level.currentXp,
                    xpForNextLevel = level.xpForNextLevel,
                )
            }
        }
        uiState.upcomingEvent?.let { event ->
            item {
                HeroImageCard(
                    imageUrl = event.imageUrl,
                    title = event.title,
                    subtitle = eventSubtitle(event),
                    onClick = { onEventSelected(event.id) },
                )
            }
        }
        item {
            QuickActionsRow(onDestinationSelected = onDestinationSelected)
        }
        item {
            SectionHeader(
                title = stringResource(R.string.your_tasks),
                actionLabel = stringResource(CoreR.string.see_more),
                onAction = onAllTasksClick,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
        }
        when {
            uiState.hasTasksError -> item {
                ErrorContent(
                    message = stringResource(CoreR.string.error_generic_message),
                    retryLabel = stringResource(CoreR.string.retry_button),
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            uiState.tasks.isEmpty() -> item {
                EmptyContent(
                    message = stringResource(R.string.all_tasks_completed),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            else -> items(uiState.tasks, key = { it.id }) { task ->
                GpListRow(
                    title = task.title,
                    subtitle = stringResource(CoreR.string.points_reward, task.rewardPoints),
                    leading = { MascotWidget(size = Dimens.IconSizeExtraLarge) },
                    onClick = { onTaskSelected(task.id) },
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    displayName: String?,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = currentLocale()
    val today = remember(locale) {
        LocalDate.now()
            .format(DateTimeFormatter.ofPattern(TODAY_PATTERN, locale))
            .replaceFirstChar { it.titlecase(locale) }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = today,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = if (displayName != null) {
                    stringResource(R.string.hello_name, displayName)
                } else {
                    stringResource(R.string.hello)
                },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Surface(
            onClick = onProfileClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.size(Dimens.AvatarSize),
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = stringResource(R.string.profile),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(Dimens.SpacingMedium),
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    onDestinationSelected: (Destination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val tileWidth = remember(screenWidth) {
        val available = screenWidth - 2 * Dimens.ScreenHorizontalPadding.value -
            (QUICK_ACTIONS_VISIBLE - 1) * Dimens.SpacingMedium.value
        available / QUICK_ACTIONS_VISIBLE
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
    ) {
        items(HomeQuickAction.entries) { action ->
            QuickActionTile(
                label = stringResource(action.labelRes),
                icon = action.icon,
                onClick = { onDestinationSelected(action.destination) },
                modifier = Modifier.width(tileWidth.dp),
            )
        }
    }
}

@Composable
private fun eventSubtitle(event: EcoEvent): String {
    val locale = currentLocale()
    return stringResource(
        R.string.date_time_place,
        formatEventDate(event.startAtEpochMillis, locale),
        formatEventTime(event.startAtEpochMillis, locale),
        event.location,
    )
}

@Composable
private fun currentLocale(): Locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()
