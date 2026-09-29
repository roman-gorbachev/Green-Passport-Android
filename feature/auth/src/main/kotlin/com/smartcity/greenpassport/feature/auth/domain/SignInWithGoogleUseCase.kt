package com.smartcity.greenpassport.feature.auth.domain

import android.content.Context
import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.auth.GoogleIdTokenRequester
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val googleIdTokenRequester: GoogleIdTokenRequester,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(activityContext: Context): AuthSession {
        val idToken = googleIdTokenRequester.requestIdToken(activityContext)
        return authRepository.signInWithGoogle(idToken)
    }
}
