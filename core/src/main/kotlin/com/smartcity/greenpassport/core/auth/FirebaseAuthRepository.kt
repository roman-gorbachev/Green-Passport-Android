package com.smartcity.greenpassport.core.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

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

    override suspend fun signInAnonymously(): AuthSession {
        val result = firebaseAuth.signInAnonymously().await()
        return result.requireUser().toAuthSession()
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthSession {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.requireUser().toAuthSession()
    }

    override suspend fun registerWithEmail(email: String, password: String): AuthSession {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return result.requireUser().toAuthSession()
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}

private fun AuthResult.requireUser(): FirebaseUser =
    user ?: error("Firebase returned a successful auth result without a user")

private fun FirebaseUser.toAuthSession() = AuthSession(userId = uid, isAnonymous = isAnonymous)
