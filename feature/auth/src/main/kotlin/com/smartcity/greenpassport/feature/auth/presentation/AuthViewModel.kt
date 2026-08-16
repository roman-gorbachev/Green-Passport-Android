package com.smartcity.greenpassport.feature.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.feature.auth.domain.RegisterWithEmailUseCase
import com.smartcity.greenpassport.feature.auth.domain.SignInAnonymouslyUseCase
import com.smartcity.greenpassport.feature.auth.domain.SignInWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInAnonymously: SignInAnonymouslyUseCase,
    private val signInWithEmail: SignInWithEmailUseCase,
    private val registerWithEmail: RegisterWithEmailUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, hasError = false) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, hasError = false) }
    }

    fun signIn() {
        val state = _uiState.value
        launchAuthAction { signInWithEmail(state.email, state.password) }
    }

    fun register() {
        val state = _uiState.value
        launchAuthAction { registerWithEmail(state.email, state.password) }
    }

    fun continueAnonymously() {
        launchAuthAction { signInAnonymously() }
    }

    private fun launchAuthAction(action: suspend () -> AuthSession) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            try {
                action()
                _uiState.update { it.copy(isLoading = false, isSignedIn = true) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e(TAG, "Auth action failed", error)
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }
}
