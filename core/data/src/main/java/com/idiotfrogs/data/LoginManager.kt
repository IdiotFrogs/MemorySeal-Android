package com.idiotfrogs.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.idiotfrogs.extension.findActivity
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

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

        val credentialData = response.credential.data
        // TODO: googleIdTokenCredential 사용처에 맞게 처리
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData)

        /**
         * ex)
         * googleIdTokenCredential.idToken -> backend단에 넘겨줄 idToken 값
         *
         * googleIdTokenCredential.profilePictureUri,
         * googleIdTokenCredential.phoneNumber,
         * etc..
         * -> googleIdTokenCredential로 얻어올 수 있는 값
         *
         * feature단에 token을 직접 넘겨주지 말아야 함
         * -> Oauth 관련 로직을 모두 LoginManager에 위임하기 위함
         * -> 관심사 분리, Oauth 세부 로직은 LoginManager에서만 처리
         */
    }

    fun appleLogin() {
        val provider = OAuthProvider.newBuilder("apple.com")
        // TODO: 필요한 기본 값 범위를 넘는 OAuth 2.0 범위 추가 지정 (필요한 경우)
        // TODO: 로그인 화면 언어를 추가로 설정해줘야 하는 경우 locale 설정
        val auth = FirebaseAuth.getInstance()
        val pending = auth.pendingAuthResult

        // 대기 중인 결과가 존재하는 경우
        if (pending != null) {
            pending.addOnSuccessListener { authResult ->
                // TODO: authResult 사용처에 맞게 처리
                authResult.user

                /**
                 * ex)
                 * authResult.user?.getIdToken(false)?.result?.token -> backend단에 넘겨줄 idToken 값
                 *
                 * authResult.user?.photoUrl
                 * authResult.user?.phoneNumber,
                 * etc..
                 * -> authResult.getUser()를 통해 얻어올 수 있는 값
                 *
                 * feature단에 token을 직접 넘겨주지 말아야 함
                 * -> Oauth 관련 로직을 모두 LoginManager에 위임하기 위함
                 * -> 관심사 분리, Oauth 세부 로직은 LoginManager에서만 처리
                 */
            }.addOnFailureListener { exception ->
                throw exception
            }
        } else {
            context.findActivity()?.let {
                auth.startActivityForSignInWithProvider(it, provider.build())
                    .addOnSuccessListener { authResult ->
                        // 위 addOnSuccessListener와 동일하게 처리
                    }
                    .addOnFailureListener { exception ->
                        throw exception
                    }
            } ?: run {
                throw Exception("activity is not available")
            }
        }
    }
}