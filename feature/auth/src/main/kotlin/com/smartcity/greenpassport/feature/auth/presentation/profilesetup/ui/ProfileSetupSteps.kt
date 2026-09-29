package com.smartcity.greenpassport.feature.auth.presentation.profilesetup.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import com.smartcity.greenpassport.core.designsystem.component.GpFilterChip
import com.smartcity.greenpassport.core.designsystem.component.GpTextField
import com.smartcity.greenpassport.core.designsystem.component.ProfileAvatar
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.core.model.profile.AvatarStyle
import com.smartcity.greenpassport.core.model.profile.SupportedCities
import com.smartcity.greenpassport.feature.auth.R
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state.NameError
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state.ProfileSetupUiState
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state.interestLabelRes
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state.nameErrorMessageRes

@Composable
fun NameStep(
    uiState: ProfileSetupUiState,
    actions: ProfileStepActions,
    modifier: Modifier = Modifier,
) {
    StepColumn(
        title = stringResource(R.string.what_is_your_name),
        subtitle = stringResource(R.string.name_shown_to_community_msg),
        modifier = modifier,
    ) {
        NameField(
            value = uiState.firstName,
            onValueChange = actions.onFirstNameChange,
            labelRes = R.string.first_name,
            error = uiState.firstNameError,
            imeAction = ImeAction.Next,
            onDone = {},
        )
        NameField(
            value = uiState.lastName,
            onValueChange = actions.onLastNameChange,
            labelRes = R.string.last_name,
            error = uiState.lastNameError,
            imeAction = ImeAction.Done,
            onDone = actions.onNext,
            modifier = Modifier.padding(top = Dimens.SpacingMedium),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CityStep(
    uiState: ProfileSetupUiState,
    actions: ProfileStepActions,
    modifier: Modifier = Modifier,
) {
    StepColumn(
        title = stringResource(R.string.your_city),
        subtitle = stringResource(R.string.we_show_tasks_and_events_nearby_msg),
        error = if (uiState.isCityMissing) stringResource(R.string.choose_city) else null,
        modifier = modifier,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            modifier = Modifier.fillMaxWidth(),
        ) {
            SupportedCities.all.forEach { city ->
                GpFilterChip(
                    label = city,
                    selected = uiState.city == city,
                    onClick = { actions.onCitySelected(city) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestsStep(
    uiState: ProfileSetupUiState,
    actions: ProfileStepActions,
    modifier: Modifier = Modifier,
) {
    StepColumn(
        title = stringResource(R.string.what_interests_you),
        subtitle = stringResource(R.string.choose_one_or_more_msg),
        error = if (uiState.isInterestsMissing) stringResource(R.string.choose_at_least_one) else null,
        modifier = modifier,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            modifier = Modifier.fillMaxWidth(),
        ) {
            TaskCategory.entries.forEach { category ->
                GpFilterChip(
                    label = stringResource(interestLabelRes(category)),
                    selected = category in uiState.interests,
                    onClick = { actions.onInterestToggled(category) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AvatarStep(
    uiState: ProfileSetupUiState,
    actions: ProfileStepActions,
    modifier: Modifier = Modifier,
) {
    StepColumn(
        title = stringResource(R.string.choose_avatar),
        subtitle = stringResource(R.string.avatar_can_be_changed_later_msg),
        modifier = modifier,
    ) {
        ProfileAvatar(style = uiState.avatar, size = Dimens.EmptyStateMascotSize)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.SpacingLarge),
        ) {
            AvatarStyle.entries.forEach { style ->
                val isSelected = style == uiState.avatar
                ProfileAvatar(
                    style = style,
                    size = Dimens.IconCircleSize,
                    modifier = Modifier
                        .clip(CircleShape)
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    border = BorderStroke(
                                        width = Dimens.SelectionBorderWidth,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    ),
                                    shape = CircleShape,
                                )
                            } else {
                                Modifier
                            },
                        )
                        .clickable(role = Role.RadioButton) { actions.onAvatarSelected(style) },
                )
            }
        }
    }
}

@Composable
private fun StepColumn(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Dimens.SpacingSmall, bottom = Dimens.SpacingLarge),
        )
        content()
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            )
        }
    }
}

@Composable
private fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
    labelRes: Int,
    error: NameError?,
    imeAction: ImeAction,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GpTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelRes)) },
        isError = error != null,
        supportingText = if (error != null) {
            { Text(stringResource(nameErrorMessageRes(error))) }
        } else {
            null
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = modifier.fillMaxWidth(),
    )
}
