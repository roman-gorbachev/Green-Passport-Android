package com.smartcity.greenpassport.core.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    override val session: Flow<AuthSession?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toAuthSession())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signInAnonymously(): AuthSession = mapAuthFailures {
        firebaseAuth.signInAnonymously().await().requireUser().toAuthSession()
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthSession = mapAuthFailures {
        firebaseAuth.signInWithEmailAndPassword(email, password).await().requireUser().toAuthSession()
    }

    override suspend fun registerWithEmail(email: String, password: String): AuthSession = mapAuthFailures {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await().requireUser().toAuthSession()
    }

    override suspend fun signInWithGoogle(idToken: String): AuthSession = mapAuthFailures {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await().requireUser().toAuthSession()
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    private suspend fun <T> mapAuthFailures(block: suspend () -> T): T =
        runCatching { block() }.getOrElse { error ->
            if (error is CancellationException) throw error
            throw AuthFailureException(error.toAuthFailure(), error)
        }

    private fun Throwable.toAuthFailure(): AuthFailure = when {
        this is FirebaseNetworkException -> AuthFailure.NETWORK
        this is FirebaseTooManyRequestsException -> AuthFailure.TOO_MANY_REQUESTS
        this is FirebaseAuthWeakPasswordException -> AuthFailure.WEAK_PASSWORD
        this is FirebaseAuthUserCollisionException -> AuthFailure.EMAIL_ALREADY_IN_USE
        this is FirebaseAuthInvalidUserException -> AuthFailure.INVALID_CREDENTIALS
        this is FirebaseAuthInvalidCredentialsException && errorCode == ERROR_INVALID_EMAIL -> AuthFailure.INVALID_EMAIL
        this is FirebaseAuthInvalidCredentialsException -> AuthFailure.INVALID_CREDENTIALS
        this is FirebaseAuthException && errorCode == ERROR_OPERATION_NOT_ALLOWED -> AuthFailure.SIGN_IN_METHOD_DISABLED
        message.orEmpty().contains(CONFIGURATION_NOT_FOUND) -> AuthFailure.SIGN_IN_METHOD_DISABLED
        else -> AuthFailure.UNKNOWN
    }

    companion object {
        private const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
        private const val ERROR_OPERATION_NOT_ALLOWED = "ERROR_OPERATION_NOT_ALLOWED"
        private const val CONFIGURATION_NOT_FOUND = "CONFIGURATION_NOT_FOUND"
    }
}

private fun AuthResult.requireUser(): FirebaseUser =
    user ?: error("Firebase returned a successful auth result without a user")

private fun FirebaseUser.toAuthSession() = AuthSession(
    userId = uid,
    email = email,
    isAnonymous = isAnonymous,
    displayName = displayName?.takeIf { it.isNotBlank() },
    isGoogleAccount = providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID },
)
