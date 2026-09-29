package com.smartcity.greenpassport.core.auth

import android.annotation.SuppressLint
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class GoogleIdTokenRequester @Inject constructor() {

    suspend fun requestIdToken(activityContext: Context): String {
        val webClientId = requireWebClientId(activityContext)
        val option = GetSignInWithGoogleOption.Builder(webClientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        return runCatching {
            val response = CredentialManager.create(activityContext).getCredential(activityContext, request)
            GoogleIdTokenCredential.createFrom(response.credential.data).idToken
        }.getOrElse { error ->
            if (error is CancellationException) throw error
            val failure = if (error is GetCredentialCancellationException) {
                AuthFailure.GOOGLE_CANCELLED
            } else {
                AuthFailure.GOOGLE_UNAVAILABLE
            }
            throw AuthFailureException(failure, error)
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun requireWebClientId(context: Context): String {
        val resId = context.resources.getIdentifier(WEB_CLIENT_ID_RESOURCE, STRING_RESOURCE_TYPE, context.packageName)
        val webClientId = if (resId == 0) null else context.getString(resId).takeIf { it.isNotBlank() }
        return webClientId
            ?: throw AuthFailureException(AuthFailure.GOOGLE_UNAVAILABLE, IllegalStateException(MISSING_CLIENT_ID))
    }

    companion object {
        private const val WEB_CLIENT_ID_RESOURCE = "default_web_client_id"
        private const val STRING_RESOURCE_TYPE = "string"
        private const val MISSING_CLIENT_ID = "default_web_client_id is missing: enable Google sign-in in Firebase " +
            "and download a fresh google-services.json"
    }
}
