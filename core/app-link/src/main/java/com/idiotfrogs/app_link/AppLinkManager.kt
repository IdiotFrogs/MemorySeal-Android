package com.idiotfrogs.app_link

import android.net.Uri
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleInviteCodeUseCase
import com.idiotfrogs.domain.usecase.timecapsule.JoinTimeCapsuleUseCase
import com.idiotfrogs.util.exception.AlreadyContributorException
import javax.inject.Inject

class AppLinkManager @Inject constructor(
    private val getTimeCapsuleInviteCodeUseCase: GetTimeCapsuleInviteCodeUseCase,
    private val joinTimeCapsuleUseCase: JoinTimeCapsuleUseCase,
) {
    suspend fun createInviteLink(capsuleId: Long): Result<String> =
        getTimeCapsuleInviteCodeUseCase(capsuleId).map { response ->
            Uri.parse(APP_LINK_BASE_URL)
                .buildUpon()
                .appendQueryParameter(CODE_PARAMETER, response.code)
                .appendQueryParameter(ACTION_PARAMETER, INVITE_ACTION)
                .appendQueryParameter(CAPSULE_ID_PARAMETER, capsuleId.toString())
                .build()
                .toString()
        }

    fun parseAppLink(uri: Uri): AppLink? {
        val isSupportedUrl =
            uri.scheme.equals(APP_LINK_SCHEME, ignoreCase = true) &&
                uri.host.equals(APP_LINK_HOST, ignoreCase = true)

        if (!isSupportedUrl) return null

        return when (uri.getQueryParameter(ACTION_PARAMETER)) {
            INVITE_ACTION -> {
                uri.getQueryParameter(CAPSULE_ID_PARAMETER)
                    ?.toLongOrNull()
                    ?.let { AppLink.Invite(capsuleId = it) }
            }
            else -> null
        }
    }

    suspend fun joinInvite(capsuleId: Long): Result<JoinInviteResult> =
        joinTimeCapsuleUseCase(capsuleId).fold(
            onSuccess = {
                Result.success<JoinInviteResult>(JoinInviteResult.Joined)
            },
            onFailure = { exception ->
                when (exception) {
                    is AlreadyContributorException -> {
                        Result.success<JoinInviteResult>(JoinInviteResult.AlreadyJoined)
                    }
                    else -> Result.failure(exception)
                }
            },
        )

    companion object {
        private const val APP_LINK_SCHEME = "https"
        private const val APP_LINK_HOST = "memory-seal-front.vercel.app"
        private const val APP_LINK_BASE_URL = "$APP_LINK_SCHEME://$APP_LINK_HOST/"

        private const val CODE_PARAMETER = "code"
        private const val ACTION_PARAMETER = "action"
        private const val CAPSULE_ID_PARAMETER = "capsuleId"
        private const val INVITE_ACTION = "invite"
    }
}
