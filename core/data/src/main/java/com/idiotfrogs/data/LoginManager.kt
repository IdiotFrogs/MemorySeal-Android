package com.idiotfrogs.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWebException
import com.google.firebase.auth.OAuthProvider
import com.idiotfrogs.domain.exception.LoginCancelledException
import com.idiotfrogs.extension.findActivity
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@ActivityScoped
class LoginManager @Inject constructor(
    // 필요한 파라미터? (ex. 내부저장소 로직)
    @ActivityContext private val context: Context
) {
    suspend fun googleLogin() {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val response = credentialManager.getCredential(
            request = request,
            context = context
        )

        when (val credential = response.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        // TODO: idToken 관련 처리
                    } catch (exception: Exception) {
                        throw exception
                    }
                } else {
                    throw Exception("Unexpected type of credential")
                }
            }
            else -> {
                throw Exception("Unexpected type of credential")
            }
        }
    }

    suspend fun appleLogin() = suspendCancellableCoroutine { continuation ->
        // TODO: 필요한 기본 값 범위를 넘는 OAuth 2.0 범위 추가 지정 (필요한 경우)
        // TODO: 로그인 화면 언어를 추가로 설정해줘야 하는 경우 locale 설정
        val provider = OAuthProvider.newBuilder("apple.com")
        val auth = FirebaseAuth.getInstance()
        val pending = auth.pendingAuthResult

        // 대기 중인 결과가 존재하는 경우
        if (pending != null) {
            pending.addOnSuccessListener { authResult ->
                authResult.sendUid()
                continuation.resume(Unit)
            }.addOnFailureListener { exception ->
                if (exception is FirebaseAuthWebException) {
                    continuation.resumeWithException(LoginCancelledException())
                } else {
                    continuation.resumeWithException(exception)
                }
            }
        } else {
            context.findActivity()?.let {
                auth.startActivityForSignInWithProvider(it, provider.build())
                    .addOnSuccessListener { authResult ->
                        authResult.sendUid()
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        if (exception is FirebaseAuthWebException) {
                            continuation.resumeWithException(LoginCancelledException())
                        } else {
                            continuation.resumeWithException(exception)
                        }
                    }
            } ?: run {
                throw Exception("Activity is not available")
            }
        }
    }

    private fun AuthResult.sendUid() {
        this.user?.providerData?.takeIf { it.isNotEmpty() }?.let { providerData ->
            providerData.forEach { userInfo ->
                if (userInfo.providerId == "apple.com") {
                    // TODO: uid 관련 처리
                    return@let
                }
            }
        } ?: run {
            throw Exception("User or ProviderData is null")
        }
    }
}