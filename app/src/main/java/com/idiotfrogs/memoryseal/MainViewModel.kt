package com.idiotfrogs.memoryseal

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idiotfrogs.app_link.AppLink
import com.idiotfrogs.app_link.AppLinkManager
import com.idiotfrogs.app_link.JoinInviteResult
import com.idiotfrogs.util.exception.LoginRequiredException
import com.idiotfrogs.util.sideEffect.AppSideEffect
import com.idiotfrogs.util.sideEffect.MSSideEffect
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val appLinkManager: AppLinkManager,
) : ViewModel() {
    private val _event = MutableSharedFlow<MainEvent>()
    val event = _event.asSharedFlow()

    private val _navigationEvent = Channel<MainNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    val pendingInviteCapsuleId = savedStateHandle.getStateFlow<Long?>(
        PENDING_INVITE_CAPSULE_ID_KEY,
        null,
    )

    fun collectAppSideEffect() {
        viewModelScope.launch {
            MSSideEffect.appSideEffect.collect { sideEffect ->
                when (sideEffect) {
                    AppSideEffect.LoginRequired -> _event.emit(MainEvent.NavigateToLogin)
                }
            }
        }
    }

    fun onPushReceived(
        type: String?,
        capsuleId: String?,
    ) {
        val id = capsuleId?.toLongOrNull() ?: return

        val event = when (type) {
            "member" -> MainNavigationEvent.NavigateToFriend(id)
            "detail" -> MainNavigationEvent.NavigateToDetail(id)
            "open" -> MainNavigationEvent.NavigateToPreview(id)
            else -> return
        }

        _navigationEvent.trySend(event)
    }

    fun onAppLinkReceived(uri: Uri) {
        when (val appLink = appLinkManager.parseAppLink(uri)) {
            is AppLink.Invite -> {
                savedStateHandle[PENDING_INVITE_CAPSULE_ID_KEY] = appLink.capsuleId
            }
            null -> Unit
        }
    }

    fun joinPendingInvite() {
        val capsuleId = pendingInviteCapsuleId.value ?: return
        viewModelScope.launch {
            try {
                val result = appLinkManager.joinInvite(capsuleId)

                if (result.isFailure) {
                    clearPendingInvite()
                    _event.emit(MainEvent.ShowToast(INVITE_FAILURE_MESSAGE))
                    return@launch
                }

                when (result.getOrThrow()) {
                    JoinInviteResult.Joined -> completeInvite(capsuleId)
                    JoinInviteResult.AlreadyJoined -> {
                        completeInvite(
                            capsuleId = capsuleId,
                            toastMessage = ALREADY_CONTRIBUTOR_MESSAGE,
                        )
                    }
                }
            } catch (_: LoginRequiredException) {
                _event.emit(MainEvent.NavigateToLogin)
            }
        }
    }

    private suspend fun completeInvite(
        capsuleId: Long,
        toastMessage: String? = null,
    ) {
        clearPendingInvite()
        RefreshSideEffect.tryEmit(RefreshEvent.Home)
        _navigationEvent.send(
            MainNavigationEvent.NavigateToDetail(
                capsuleId = capsuleId,
                toastMessage = toastMessage,
            ),
        )
    }

    private fun clearPendingInvite() {
        savedStateHandle[PENDING_INVITE_CAPSULE_ID_KEY] = null
    }

    companion object {
        private const val PENDING_INVITE_CAPSULE_ID_KEY = "pendingInviteCapsuleId"
        private const val ALREADY_CONTRIBUTOR_MESSAGE = "이미 참여한 타임캡슐이에요."
        private const val INVITE_FAILURE_MESSAGE = "타임캡슐 참여에 실패했어요. 다시 시도해 주세요."
    }
}

sealed interface MainEvent {
    data object NavigateToLogin : MainEvent
    data class ShowToast(val message: String) : MainEvent
}

sealed interface MainNavigationEvent {
    data class NavigateToFriend(val capsuleId: Long) : MainNavigationEvent
    data class NavigateToDetail(
        val capsuleId: Long,
        val toastMessage: String? = null,
    ) : MainNavigationEvent
    data class NavigateToPreview(val capsuleId: Long) : MainNavigationEvent
}
