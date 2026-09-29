package com.smartcity.greenpassport.feature.auth.presentation.auth.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.GpTextField
import com.smartcity.greenpassport.core.designsystem.component.MascotWidget
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.auth.R
import com.smartcity.greenpassport.feature.auth.presentation.auth.state.AuthMode
import com.smartcity.greenpassport.feature.auth.presentation.auth.state.AuthUiState
import com.smartcity.greenpassport.feature.auth.presentation.auth.state.authFailureMessageRes
import com.smartcity.greenpassport.feature.auth.presentation.auth.viewmodels.AuthViewModel
import com.smartcity.greenpassport.feature.auth.presentation.components.GoogleSignInButton
import com.smartcity.greenpassport.feature.auth.presentation.components.StepIndicator
import com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state.ProfileSetupStep

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    AuthContent(
        uiState = uiState,
        actions = AuthActions(
            onModeChange = viewModel::onModeChange,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
            onSubmit = viewModel::onSubmit,
            onGoogle = { viewModel.onSignInWithGoogle(context) },
            onContinueAnonymously = viewModel::continueAnonymously,
        ),
        modifier = modifier,
    )
}

private class AuthActions(
    val onModeChange: (AuthMode) -> Unit,
    val onEmailChange: (String) -> Unit,
    val onPasswordChange: (String) -> Unit,
    val onConfirmPasswordChange: (String) -> Unit,
    val onTogglePasswordVisibility: () -> Unit,
    val onSubmit: () -> Unit,
    val onGoogle: () -> Unit,
    val onContinueAnonymously: () -> Unit,
)

@Composable
private fun AuthContent(
    uiState: AuthUiState,
    actions: AuthActions,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = uiState.mode,
        transitionSpec = {
            val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
            (slideInHorizontally { width -> width * direction } + fadeIn()) togetherWith
                (slideOutHorizontally { width -> -width * direction } + fadeOut())
        },
        label = "authMode",
        modifier = modifier.fillMaxSize(),
    ) { mode ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingLarge),
        ) {
            if (mode == AuthMode.REGISTER) {
                StepIndicator(stepCount = ProfileSetupStep.TOTAL_STEPS, currentStep = 0)
            }
            MascotWidget(
                size = Dimens.EmptyStateMascotSize,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            )
            Text(
                text = stringResource(if (mode == AuthMode.SIGN_IN) R.string.sign_in else R.string.create_account),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            )
            CredentialsFields(uiState = uiState, mode = mode, actions = actions)
            uiState.failure?.let { failure ->
                Text(
                    text = stringResource(authFailureMessageRes(failure)),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = Dimens.SpacingMedium),
                )
            }
            GpPrimaryButton(
                text = stringResource(if (mode == AuthMode.SIGN_IN) R.string.log_in else R.string.next),
                onClick = actions.onSubmit,
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(top = Dimens.SpacingLarge),
            )
            Text(
                text = stringResource(R.string.or),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(vertical = Dimens.SpacingMedium),
            )
            GoogleSignInButton(onClick = actions.onGoogle, enabled = !uiState.isLoading)
            AuthFooter(mode = mode, isLoading = uiState.isLoading, actions = actions)
        }
    }
}

@Composable
private fun CredentialsFields(
    uiState: AuthUiState,
    mode: AuthMode,
    actions: AuthActions,
) {
    val passwordTransformation = if (uiState.isPasswordVisible) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }

    GpTextField(
        value = uiState.email,
        onValueChange = actions.onEmailChange,
        label = { Text(stringResource(R.string.email)) },
        isError = uiState.isEmailInvalid,
        supportingText = errorText(uiState.isEmailInvalid, R.string.enter_valid_email),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.SpacingLarge),
    )
    GpTextField(
        value = uiState.password,
        onValueChange = actions.onPasswordChange,
        label = { Text(stringResource(R.string.password)) },
        isError = uiState.isPasswordTooShort,
        supportingText = errorText(uiState.isPasswordTooShort, R.string.password_at_least_6_characters),
        trailingIcon = {
            IconButton(onClick = actions.onTogglePasswordVisibility) {
                Icon(
                    imageVector = if (uiState.isPasswordVisible) {
                        Icons.Filled.VisibilityOff
                    } else {
                        Icons.Filled.Visibility
                    },
                    contentDescription = stringResource(R.string.show_password),
                )
            }
        },
        singleLine = true,
        visualTransformation = passwordTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = if (mode == AuthMode.REGISTER) ImeAction.Next else ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { actions.onSubmit() }),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.SpacingMedium),
    )
    if (mode == AuthMode.REGISTER) {
        GpTextField(
            value = uiState.confirmPassword,
            onValueChange = actions.onConfirmPasswordChange,
            label = { Text(stringResource(R.string.repeat_password)) },
            isError = uiState.isPasswordMismatch,
            supportingText = errorText(uiState.isPasswordMismatch, R.string.passwords_do_not_match),
            singleLine = true,
            visualTransformation = passwordTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { actions.onSubmit() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.SpacingMedium),
        )
    }
}

@Composable
private fun AuthFooter(
    mode: AuthMode,
    isLoading: Boolean,
    actions: AuthActions,
) {
    TextButton(
        onClick = {
            actions.onModeChange(if (mode == AuthMode.SIGN_IN) AuthMode.REGISTER else AuthMode.SIGN_IN)
        },
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.SpacingSmall),
    ) {
        Text(
            text = stringResource(
                if (mode == AuthMode.SIGN_IN) R.string.no_account_create_one else R.string.already_have_account_sign_in,
            ),
            style = MaterialTheme.typography.titleSmall,
        )
    }
    if (mode == AuthMode.SIGN_IN) {
        TextButton(
            onClick = actions.onContinueAnonymously,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.continue_without_account), style = MaterialTheme.typography.titleSmall)
        }
    }
}

private fun errorText(isShown: Boolean, messageRes: Int): (@Composable () -> Unit)? =
    if (isShown) {
        { Text(stringResource(messageRes)) }
    } else {
        null
    }
