package com.smartcity.greenpassport.feature.auth.presentation.ui

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
import com.smartcity.greenpassport.feature.auth.presentation.state.AuthUiState
import com.smartcity.greenpassport.feature.auth.presentation.state.authFailureMessageRes
import com.smartcity.greenpassport.feature.auth.presentation.viewmodels.AuthViewModel

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AuthContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onSignIn = viewModel::signIn,
        onRegister = viewModel::register,
        onContinueAnonymously = viewModel::continueAnonymously,
        modifier = modifier,
    )
}

@Composable
private fun AuthContent(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSignIn: () -> Unit,
    onRegister: () -> Unit,
    onContinueAnonymously: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MascotWidget(size = Dimens.EmptyStateMascotSize)
        Text(
            text = stringResource(R.string.sign_in),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = Dimens.SpacingMedium),
        )
        EmailField(
            email = uiState.email,
            isInvalid = uiState.isEmailInvalid,
            onEmailChange = onEmailChange,
            modifier = Modifier.padding(top = Dimens.SpacingLarge),
        )
        PasswordField(
            password = uiState.password,
            isVisible = uiState.isPasswordVisible,
            isTooShort = uiState.isPasswordTooShort,
            onPasswordChange = onPasswordChange,
            onToggleVisibility = onTogglePasswordVisibility,
            onDone = onSignIn,
            modifier = Modifier.padding(top = Dimens.SpacingMedium),
        )
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
            text = stringResource(R.string.log_in),
            onClick = onSignIn,
            isLoading = uiState.isLoading,
            modifier = Modifier.padding(top = Dimens.SpacingLarge),
        )
        AuthTextButton(
            text = stringResource(R.string.create_account),
            enabled = !uiState.isLoading,
            onClick = onRegister,
        )
        AuthTextButton(
            text = stringResource(R.string.continue_without_account),
            enabled = !uiState.isLoading,
            onClick = onContinueAnonymously,
        )
    }
}

@Composable
private fun EmailField(
    email: String,
    isInvalid: Boolean,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    GpTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text(stringResource(R.string.email)) },
        isError = isInvalid,
        supportingText = if (isInvalid) {
            { Text(stringResource(R.string.enter_valid_email)) }
        } else {
            null
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun PasswordField(
    password: String,
    isVisible: Boolean,
    isTooShort: Boolean,
    onPasswordChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GpTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(stringResource(R.string.password)) },
        isError = isTooShort,
        supportingText = if (isTooShort) {
            { Text(stringResource(R.string.password_at_least_6_characters)) }
        } else {
            null
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = stringResource(R.string.show_password),
                )
            }
        },
        singleLine = true,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun AuthTextButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Dimens.SpacingSmall),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleSmall)
    }
}
