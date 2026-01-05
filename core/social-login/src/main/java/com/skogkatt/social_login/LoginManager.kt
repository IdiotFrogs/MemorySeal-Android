package com.skogkatt.social_login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWebException
import com.google.firebase.auth.OAuthProvider
import com.idiotfrogs.data.datasource.auth.AuthDataSource
import com.idiotfrogs.data.exception.LoginCancelledException
import com.idiotfrogs.extension.findActivity
import com.idiotfrogs.model.auth.AuthTokenRequest
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@ActivityScoped
class LoginManager @Inject constructor(
    // 필요한 파라미터? (ex. 내부저장소 로직)
    @ActivityContext private val context: Context,
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
        } catch (_: GetCredentialCancellationException) {
            throw LoginCancelledException()
        } catch (exception: Exception) {
            throw exception
        }
    }

    suspend fun appleLogin() = suspendCancellableCoroutine { continuation ->
        // TODO: 필요한 기본 값 범위를 넘는 OAuth 2.0 범위 추가 지정 (필요한 경우)
        // TODO: 로그인 화면 언어를 추가로 설정해줘야 하는 경우 locale 설정
        val provider = OAuthProvider.newBuilder("apple.com")
        val auth = FirebaseAuth.getInstance()
        val pending = auth.pendingAuthResult

        val task = pending.takeIf { it != null } ?: run {
            val activity = context.findActivity() ?: throw  Exception("Activity is not available")
            auth.startActivityForSignInWithProvider(activity, provider.build())
        }

        task.addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                authResult.result.sendUid()
                continuation.resume(Unit)
            } else {
                val exception = requireNotNull(authResult.exception) { "Unknown Error" }
                if (exception is FirebaseAuthWebException &&
                    exception.errorCode == ERROR_WEB_CONTEXT_CANCELED) {
                    continuation.resumeWithException(LoginCancelledException())
                } else {
                    continuation.resumeWithException(exception)
                }
            }
        }
    }

    companion object {
        const val ERROR_WEB_CONTEXT_CANCELED = "ERROR_WEB_CONTEXT_CANCELED"

        private fun AuthResult.sendUid() {
            this.user?.providerData?.takeIf { it.isNotEmpty() }?.let { providerData ->
                providerData.forEach { userInfo ->
                    if (userInfo.providerId == "apple.com") {
                        // TODO: uid 관련 처리
                        return@let
                    }
                }
            } ?: run {
                // User 또는 ProviderData가 null인 케이스
                throw Exception("User or ProviderData is null")
            }
        }
    }
}