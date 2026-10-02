package com.smartcity.greenpassport.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.R as CoreR
import com.smartcity.greenpassport.core.common.formatEventDate
import com.smartcity.greenpassport.core.common.formatEventTime
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpListRow
import com.smartcity.greenpassport.core.designsystem.component.GpSurfaceCard
import com.smartcity.greenpassport.core.designsystem.component.HeroImageCard
import com.smartcity.greenpassport.core.designsystem.component.ListRowChevron
import com.smartcity.greenpassport.core.designsystem.component.MascotWidget
import com.smartcity.greenpassport.core.designsystem.component.PointsChip
import com.smartcity.greenpassport.core.designsystem.component.ProfileAvatar
import com.smartcity.greenpassport.core.designsystem.component.ProgressHeroCard
import com.smartcity.greenpassport.core.designsystem.component.QuickActionButton
import com.smartcity.greenpassport.core.designsystem.component.SectionHeader
import com.smartcity.greenpassport.core.designsystem.component.avatarColor
import com.smartcity.greenpassport.core.designsystem.text.cityName
import com.smartcity.greenpassport.core.designsystem.text.localized
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.profile.AvatarStyle
import com.smartcity.greenpassport.core.navigation.Destination
import com.smartcity.greenpassport.feature.home.R
import com.smartcity.greenpassport.feature.home.presentation.state.HomeQuickAction
import com.smartcity.greenpassport.feature.home.presentation.state.HomeUiState
import com.smartcity.greenpassport.feature.home.presentation.viewmodels.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val TODAY_PATTERN = "EEEE, d MMM"
private const val TASK_PLACEHOLDER_COUNT = 3
private const val KEY_HEADER = "header"
private const val KEY_HERO = "hero"
private const val KEY_HERO_PLACEHOLDER = "heroPlaceholder"
private const val KEY_QUICK_ACTIONS = "quickActions"
private const val KEY_EVENT = "event"
private const val KEY_TASKS_HEADER = "tasksHeader"

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
    val systemBars = WindowInsets.systemBars.asPaddingValues()
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            top = systemBars.calculateTopPadding() + Dimens.SpacingSmall,
            bottom = systemBars.calculateBottomPadding() + Dimens.BottomBarReservedHeight + Dimens.SpacingLarge,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge),
    ) {
        item(key = KEY_HEADER) {
            HomeHeader(
                displayName = uiState.displayName,
                avatar = uiState.avatar,
                onProfileClick = onProfileClick,
                modifier = Modifier.screenPadding(),
            )
        }
        if (uiState.isLoading) {
            item(key = KEY_HERO_PLACEHOLDER) {
                ProgressHeroPlaceholder(modifier = Modifier.screenPadding())
            }
        } else {
            item(key = KEY_HERO) {
                ProgressHeroCard(
                    points = uiState.points,
                    level = uiState.level,
                    streakDays = uiState.streakDays,
                    modifier = Modifier.screenPadding(),
                )
            }
        }
        item(key = KEY_QUICK_ACTIONS) {
            QuickActionsRow(
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier.screenPadding(),
            )
        }
        uiState.upcomingEvent?.let { event ->
            item(key = KEY_EVENT) {
                HeroImageCard(
                    imageUrl = event.imageUrl,
                    title = event.title.localized(),
                    subtitle = eventSubtitle(event),
                    onClick = { onEventSelected(event.id) },
                    modifier = Modifier.screenPadding(),
                )
            }
        }
        item(key = KEY_TASKS_HEADER) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact),
                modifier = Modifier.screenPadding(),
            ) {
                SectionHeader(
                    title = stringResource(R.string.your_tasks),
                    actionLabel = stringResource(R.string.all),
                    onAction = onAllTasksClick,
                )
                HomeTasks(uiState = uiState, onTaskSelected = onTaskSelected, onRetry = onRetry)
            }
        }
    }
}

@Composable
private fun HomeTasks(
    uiState: HomeUiState,
    onTaskSelected: (String) -> Unit,
    onRetry: () -> Unit,
) {
    when {
        uiState.isLoading -> repeat(TASK_PLACEHOLDER_COUNT) {
            ListRowPlaceholder()
        }

        uiState.hasTasksError -> ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = onRetry,
        )

        uiState.tasks.isEmpty() -> EmptyContent(message = stringResource(R.string.all_tasks_completed))

        else -> uiState.tasks.forEach { task ->
            GpListRow(
                title = task.title.localized(),
                subtitle = task.city.takeIf { it.isNotBlank() }?.let { cityName(it) },
                leading = { MascotWidget(size = Dimens.ListRowMascotSize) },
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PointsChip(points = task.rewardPoints)
                        ListRowChevron(modifier = Modifier.padding(start = Dimens.SpacingSmall))
                    }
                },
                onClick = { onTaskSelected(task.id) },
            )
        }
    }
}

@Composable
private fun HomeHeader(
    displayName: String?,
    avatar: AvatarStyle,
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
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = if (displayName != null) {
                    stringResource(R.string.hello_name, displayName)
                } else {
                    stringResource(R.string.hello)
                },
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        val profileLabel = stringResource(R.string.profile)
        GpSurfaceCard(
            onClick = onProfileClick,
            shape = CircleShape,
            color = avatarColor(avatar),
            modifier = Modifier
                .size(Dimens.AvatarSize)
                .semantics { contentDescription = profileLabel },
        ) {
            ProfileAvatar(style = avatar, size = Dimens.AvatarSize)
        }
    }
}

@Composable
private fun QuickActionsRow(
    onDestinationSelected: (Destination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        HomeQuickAction.entries.forEach { action ->
            QuickActionButton(
                label = stringResource(action.labelRes),
                icon = action.icon,
                onClick = { onDestinationSelected(action.destination) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProgressHeroPlaceholder(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ProgressHeroHeight)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
            ),
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ListRowPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ListRowHeight)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large,
            ),
    )
}

private fun Modifier.screenPadding(): Modifier = padding(horizontal = Dimens.ScreenHorizontalPadding)

@Composable
private fun eventSubtitle(event: EcoEvent): String {
    val locale = currentLocale()
    return stringResource(
        R.string.date_time_place,
        formatEventDate(event.startAtEpochMillis, locale),
        formatEventTime(event.startAtEpochMillis, locale),
        event.location.localized(),
    )
}

@Composable
private fun currentLocale(): Locale = LocalLocale.current.platformLocale
