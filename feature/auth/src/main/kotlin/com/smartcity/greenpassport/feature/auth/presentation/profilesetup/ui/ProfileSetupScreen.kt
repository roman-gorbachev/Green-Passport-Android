package com.smartcity.greenpassport.feature.auth.presentation.profilesetup.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.GpBackButton
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.auth.R
import com.smartcity.greenpassport.feature.auth.presentation.components.StepIndicator
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state.ProfileSetupStep
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state.ProfileSetupUiState
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.viewmodels.ProfileSetupViewModel

@Composable
fun ProfileSetupScreen(
    isEditing: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: ProfileSetupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved && isEditing) onFinished()
    }
    BackHandler(enabled = uiState.step != ProfileSetupStep.NAME, onBack = viewModel::onBack)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingMedium),
    ) {
        SetupTopRow(
            uiState = uiState,
            isEditing = isEditing,
            onBack = viewModel::onBack,
            onSignOut = viewModel::onSignOut,
        )
        StepAnimatedContent(
            uiState = uiState,
            actions = ProfileStepActions(
                onFirstNameChange = viewModel::onFirstNameChange,
                onLastNameChange = viewModel::onLastNameChange,
                onCitySelected = viewModel::onCitySelected,
                onInterestToggled = viewModel::onInterestToggled,
                onAvatarSelected = viewModel::onAvatarSelected,
                onNext = viewModel::onNext,
            ),
        )
        if (uiState.hasSaveError) {
            Text(
                text = stringResource(R.string.could_not_save_profile_msg),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            )
        }
        GpPrimaryButton(
            text = stringResource(nextButtonRes(uiState.step, isEditing)),
            onClick = viewModel::onNext,
            isLoading = uiState.isSaving,
            enabled = uiState.isPrefilled,
            modifier = Modifier.padding(top = Dimens.SpacingLarge),
        )
    }
}

@Composable
private fun SetupTopRow(
    uiState: ProfileSetupUiState,
    isEditing: Boolean,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(modifier = Modifier.size(Dimens.BackButtonSize)) {
            if (uiState.step != ProfileSetupStep.NAME) {
                GpBackButton(onClick = onBack)
            }
        }
        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
            if (isEditing) {
                StepIndicator(stepCount = ProfileSetupStep.entries.size, currentStep = uiState.step.ordinal)
            } else {
                StepIndicator(stepCount = ProfileSetupStep.TOTAL_STEPS, currentStep = uiState.step.ordinal + 1)
            }
        }
        Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.size(Dimens.BackButtonSize)) {
            if (!isEditing && uiState.step == ProfileSetupStep.NAME) {
                TextButton(onClick = onSignOut) {
                    Text(text = stringResource(R.string.exit), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun StepAnimatedContent(
    uiState: ProfileSetupUiState,
    actions: ProfileStepActions,
) {
    AnimatedContent(
        targetState = uiState.step,
        transitionSpec = {
            val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
            (slideInHorizontally { width -> width * direction } + fadeIn()) togetherWith
                (slideOutHorizontally { width -> -width * direction } + fadeOut())
        },
        label = "profileSetupStep",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.SpacingLarge),
    ) { step ->
        when (step) {
            ProfileSetupStep.NAME -> NameStep(uiState = uiState, actions = actions)
            ProfileSetupStep.CITY -> CityStep(uiState = uiState, actions = actions)
            ProfileSetupStep.INTERESTS -> InterestsStep(uiState = uiState, actions = actions)
            ProfileSetupStep.AVATAR -> AvatarStep(uiState = uiState, actions = actions)
        }
    }
}

private fun nextButtonRes(step: ProfileSetupStep, isEditing: Boolean): Int = when {
    step != ProfileSetupStep.AVATAR -> R.string.next
    isEditing -> R.string.save
    else -> R.string.done
}
