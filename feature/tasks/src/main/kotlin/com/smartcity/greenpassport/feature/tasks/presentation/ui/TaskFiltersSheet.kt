package com.smartcity.greenpassport.feature.tasks.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.smartcity.greenpassport.core.designsystem.component.GpDropdownMenu
import com.smartcity.greenpassport.core.designsystem.component.GpDropdownMenuItem
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.ListSection
import com.smartcity.greenpassport.core.designsystem.component.ListSectionRow
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.component.SymbolTileStyle
import com.smartcity.greenpassport.core.designsystem.icon.taskCategoryIcon
import com.smartcity.greenpassport.core.designsystem.text.cityName
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.core.model.profile.SupportedCities
import com.smartcity.greenpassport.core.model.verification.TaskVerification
import com.smartcity.greenpassport.feature.tasks.R
import com.smartcity.greenpassport.feature.tasks.presentation.state.TaskCityFilter
import com.smartcity.greenpassport.feature.tasks.presentation.state.TaskFilters
import com.smartcity.greenpassport.feature.tasks.presentation.state.TaskStatusFilter
import com.smartcity.greenpassport.feature.tasks.presentation.state.statusFilterLabelRes
import com.smartcity.greenpassport.feature.tasks.presentation.state.verificationHintRes
import com.smartcity.greenpassport.feature.tasks.presentation.state.verificationIcon
import com.smartcity.greenpassport.feature.tasks.presentation.state.verificationLabelRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFiltersSheet(
    initialFilters: TaskFilters,
    profileCity: String?,
    resultCount: (TaskFilters) -> Int,
    onApply: (TaskFilters) -> Unit,
    onDismiss: () -> Unit,
) {
    var draft by remember(initialFilters) { mutableStateOf(initialFilters) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column {
            SheetHeader(canReset = draft != TaskFilters(), onReset = { draft = TaskFilters() })
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge),
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = Dimens.ScreenHorizontalPadding),
            ) {
                filterSections(draft = draft, profileCity = profileCity, onChange = { draft = it })
            }
            GpPrimaryButton(
                text = stringResource(R.string.show_tasks_count, resultCount(draft)),
                onClick = { onApply(draft) },
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingMedium),
            )
        }
    }
}

@Composable
private fun SheetHeader(
    canReset: Boolean,
    onReset: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.SpacingSmall, vertical = Dimens.SpacingExtraSmall),
    ) {
        TextButton(onClick = onReset, enabled = canReset, modifier = Modifier.align(Alignment.CenterStart)) {
            Text(text = stringResource(R.string.reset), style = MaterialTheme.typography.bodyLarge)
        }
        Text(
            text = stringResource(R.string.filters),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

private fun LazyListScope.filterSections(
    draft: TaskFilters,
    profileCity: String?,
    onChange: (TaskFilters) -> Unit,
) {
    item {
        ListSection(header = stringResource(R.string.status)) {
            TaskStatusFilter.entries.forEachIndexed { index, status ->
                ListSectionRow(
                    title = stringResource(statusFilterLabelRes(status)),
                    trailing = { CheckMark(isVisible = draft.status == status) },
                    onClick = { onChange(draft.copy(status = status)) },
                    showDivider = index < TaskStatusFilter.entries.lastIndex,
                )
            }
        }
    }
    item {
        ListSection(header = stringResource(R.string.confirmation)) {
            TaskVerification.entries.forEachIndexed { index, verification ->
                val isOn = verification in draft.verifications
                FilterCheckRow(
                    title = stringResource(verificationLabelRes(verification)),
                    subtitle = stringResource(verificationHintRes(verification)),
                    icon = verificationIcon(verification),
                    isOn = isOn,
                    onClick = {
                        val updated = if (isOn) {
                            draft.verifications - verification
                        } else {
                            draft.verifications + verification
                        }
                        onChange(draft.copy(verifications = updated))
                    },
                    showDivider = index < TaskVerification.entries.lastIndex,
                )
            }
        }
    }
    item {
        ListSection(header = stringResource(R.string.city)) {
            CityRow(selected = draft.city, profileCity = profileCity, onSelect = { onChange(draft.copy(city = it)) })
        }
    }
    item {
        ListSection(header = stringResource(R.string.category)) {
            TaskCategory.entries.forEachIndexed { index, category ->
                val isOn = category in draft.categories
                FilterCheckRow(
                    title = stringResource(taskCategoryLabelRes(category)),
                    subtitle = null,
                    icon = taskCategoryIcon(category),
                    isOn = isOn,
                    onClick = {
                        val updated = if (isOn) draft.categories - category else draft.categories + category
                        onChange(draft.copy(categories = updated))
                    },
                    showDivider = index < TaskCategory.entries.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun FilterCheckRow(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    isOn: Boolean,
    onClick: () -> Unit,
    showDivider: Boolean,
) {
    ListSectionRow(
        title = title,
        subtitle = subtitle,
        leading = { SymbolTile(icon = icon, style = if (isOn) SymbolTileStyle.Prominent else SymbolTileStyle.Accent) },
        trailing = { CheckMark(isVisible = isOn) },
        onClick = onClick,
        showDivider = showDivider,
    )
}

@Composable
private fun CityRow(
    selected: TaskCityFilter,
    profileCity: String?,
    onSelect: (TaskCityFilter) -> Unit,
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    val options = listOf(TaskCityFilter.ProfileCity, TaskCityFilter.All) + SupportedCities.all.map(TaskCityFilter::City)
    ListSectionRow(
        title = cityLabel(selected, profileCity),
        leading = { SymbolTile(icon = Icons.Filled.LocationCity) },
        trailing = {
            Box {
                Text(
                    text = stringResource(R.string.city),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                GpDropdownMenu(expanded = isMenuOpen, onDismissRequest = { isMenuOpen = false }) {
                    options.forEach { option ->
                        GpDropdownMenuItem(
                            text = cityLabel(option, profileCity),
                            icon = null,
                            onClick = {
                                isMenuOpen = false
                                onSelect(option)
                            },
                            isSelected = option == selected,
                        )
                    }
                }
            }
        },
        onClick = { isMenuOpen = true },
    )
}

@Composable
fun cityLabel(city: TaskCityFilter, profileCity: String?): String = when (city) {
    TaskCityFilter.ProfileCity -> profileCity?.takeIf {
        it.isNotBlank()
    }?.let { cityName(it) } ?: stringResource(R.string.my_city)
    TaskCityFilter.All -> stringResource(R.string.all_cities)
    is TaskCityFilter.City -> cityName(city.name)
}

@Composable
private fun CheckMark(isVisible: Boolean) {
    Row {
        if (isVisible) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}
