package com.idiotfrogs.social_login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.idiotfrogs.local.LocalDataSource
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWebException
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.idiotfrogs.extension.findActivity
import com.idiotfrogs.model.auth.AuthTokenRequest
import com.idiotfrogs.data.datasource.auth.AuthDataSource
import com.idiotfrogs.util.exception.LoginCancelledException
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ActivityScoped
class LoginManager @Inject constructor(
    @ActivityContext private val context: Context,
    private val localDataSource: LocalDataSource,
    private val authDataSource: AuthDataSource
) {
    suspend fun googleLogin() {
        val credentialManager = CredentialManager.create(context)

        val googleSignInOption = GetSignInWithGoogleOption.Builder(
            BuildConfig.WEB_CLIENT_ID
        ).build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleSignInOption)
            .build()

        try {
            val response = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = (response.credential as? CustomCredential)
                ?.takeIf { it.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL }
                ?: throw Exception("Unexpected type of credential")

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            val tokenResponse = authDataSource.socialGoogleLogin(
                AuthTokenRequest(googleIdTokenCredential.idToken)
            )

            localDataSource.setTokens(
                accessToken = tokenResponse.accessToken,
                refreshToken = tokenResponse.refreshToken,
                accessTokenExpiresIn = tokenResponse.accessTokenExpiresIn,
            )
        } catch (_: GetCredentialCancellationException) {
            // 사용자가 로그인을 취소한 경우
            throw LoginCancelledException()
        } catch (exception: Exception) {
            throw exception
        }
    }

    suspend fun appleLogin() {
        val provider = OAuthProvider.newBuilder("apple.com")
        val auth = FirebaseAuth.getInstance()
        val pending = auth.pendingAuthResult

        val task = pending.takeIf { it != null } ?: run {
            val activity = context.findActivity() ?: throw Exception("Activity is not available")
            auth.startActivityForSignInWithProvider(activity, provider.build())
        }

        try {
            val authResult = task.await()
            val credential = authResult.credential as? OAuthCredential
            val appleIdToken = requireNotNull(credential?.idToken) { "idToken is Empty!" }

            val tokenResponse = authDataSource.socialAppleLogin(
                AuthTokenRequest(appleIdToken)
            )

            localDataSource.setTokens(
                accessToken = tokenResponse.accessToken,
                refreshToken = tokenResponse.refreshToken,
                accessTokenExpiresIn = tokenResponse.accessTokenExpiresIn,
            )
        } catch (exception: Exception) {
            if (exception is FirebaseAuthWebException && exception.errorCode == ERROR_WEB_CONTEXT_CANCELED) {
                // 사용자가 로그인을 취소한 경우
                throw LoginCancelledException()
            } else {
                throw exception
            }
        }
    }

    companion object {
        const val ERROR_WEB_CONTEXT_CANCELED = "ERROR_WEB_CONTEXT_CANCELED"
    }
}