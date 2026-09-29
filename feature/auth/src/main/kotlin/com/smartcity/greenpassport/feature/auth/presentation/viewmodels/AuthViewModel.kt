package com.smartcity.greenpassport.feature.auth.presentation.viewmodels

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
import com.smartcity.greenpassport.feature.auth.presentation.state.AuthUiState
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, isEmailInvalid = false, failure = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, isPasswordTooShort = false, failure = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun signIn() {
        submit { email, password -> signInWithEmail(email, password) }
    }

    fun register() {
        submit { email, password -> registerWithEmail(email, password) }
    }

    fun continueAnonymously() {
        launchAuthAction { signInAnonymously() }
    }

    private fun submit(action: suspend (email: String, password: String) -> AuthSession) {
        val state = _uiState.value
        if (state.isLoading) return
        val email = state.email.trim()
        val isEmailInvalid = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordTooShort = state.password.length < MIN_PASSWORD_LENGTH
        if (isEmailInvalid || isPasswordTooShort) {
            _uiState.update { it.copy(isEmailInvalid = isEmailInvalid, isPasswordTooShort = isPasswordTooShort) }
            return
        }
        launchAuthAction { action(email, state.password) }
    }

    private fun launchAuthAction(action: suspend () -> AuthSession) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, failure = null) }
            runCatching { action() }
                .onSuccess { _uiState.value = AuthUiState() }
                .onFailure { error ->
                    Log.e(TAG, "Auth action failed", error)
                    val failure = (error as? AuthFailureException)?.failure ?: AuthFailure.UNKNOWN
                    _uiState.update { it.copy(isLoading = false, failure = failure) }
                }
        }
    }

    companion object {
        private const val TAG = "AuthViewModel"
        private const val MIN_PASSWORD_LENGTH = 6
    }
}
