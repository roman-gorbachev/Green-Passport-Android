package com.smartcity.greenpassport.feature.auth.presentation.auth.viewmodels

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthFailure
import com.smartcity.greenpassport.core.auth.AuthFailureException
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.feature.auth.domain.RegisterWithEmailUseCase
import com.smartcity.greenpassport.feature.auth.domain.SignInAnonymouslyUseCase
import com.smartcity.greenpassport.feature.auth.domain.SignInWithEmailUseCase
import com.smartcity.greenpassport.feature.auth.domain.SignInWithGoogleUseCase
import com.smartcity.greenpassport.feature.auth.presentation.auth.state.AuthMode
import com.smartcity.greenpassport.feature.auth.presentation.auth.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInAnonymously: SignInAnonymouslyUseCase,
    private val signInWithEmail: SignInWithEmailUseCase,
    private val registerWithEmail: RegisterWithEmailUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onModeChange(mode: AuthMode) {
        _uiState.update {
            it.copy(
                mode = mode,
                confirmPassword = "",
                isEmailInvalid = false,
                isPasswordTooShort = false,
                isPasswordMismatch = false,
                failure = null,
            )
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, isEmailInvalid = false, failure = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(password = value, isPasswordTooShort = false, isPasswordMismatch = false, failure = null)
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, isPasswordMismatch = false, failure = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.isLoading) return
        val email = state.email.trim()
        val isEmailInvalid = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordTooShort = state.password.length < MIN_PASSWORD_LENGTH
        val isPasswordMismatch = state.mode == AuthMode.REGISTER && state.password != state.confirmPassword
        if (isEmailInvalid || isPasswordTooShort || isPasswordMismatch) {
            _uiState.update {
                it.copy(
                    isEmailInvalid = isEmailInvalid,
                    isPasswordTooShort = isPasswordTooShort,
                    isPasswordMismatch = isPasswordMismatch,
                )
            }
            return
        }
        when (state.mode) {
            AuthMode.SIGN_IN -> launchAuthAction { signInWithEmail(email, state.password) }
            AuthMode.REGISTER -> launchAuthAction { registerWithEmail(email, state.password) }
        }
    }

    fun onSignInWithGoogle(activityContext: Context) {
        if (_uiState.value.isLoading) return
        launchAuthAction { signInWithGoogle(activityContext) }
    }

    fun continueAnonymously() {
        if (_uiState.value.isLoading) return
        launchAuthAction { signInAnonymously() }
    }

    private fun launchAuthAction(action: suspend () -> AuthSession) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, failure = null) }
            runCatching { action() }
                .onSuccess { _uiState.value = AuthUiState() }
                .onFailure { error ->
                    Log.e(TAG, "Auth action failed", error)
                    val failure = (error as? AuthFailureException)?.failure ?: AuthFailure.UNKNOWN
                    val visibleFailure = failure.takeUnless { failure == AuthFailure.GOOGLE_CANCELLED }
                    _uiState.update { it.copy(isLoading = false, failure = visibleFailure) }
                }
        }
    }

    companion object {
        private const val TAG = "AuthViewModel"
        private const val MIN_PASSWORD_LENGTH = 6
    }
}
