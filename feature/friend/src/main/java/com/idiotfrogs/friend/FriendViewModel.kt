package com.idiotfrogs.friend

import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleInviteCodeUseCase
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val getTimeCapsuleInviteCodeUseCase: GetTimeCapsuleInviteCodeUseCase,
) : BaseViewModel<UiState<Unit>, FriendSideEffect, FriendAction>() {

    override val container: Container<UiState<Unit>, FriendSideEffect> = container(
        initialState = UiState.Init,
        onCreate = {
            safeLaunch {
                // TODO 초기 데이터 로딩
                intent { reduce { UiState.Success(Unit) } }
            }
        }
    )

    private fun getTimeCapsuleInviteCode(capsuleId: Long) = safeLaunch {
        getTimeCapsuleInviteCodeUseCase(capsuleId).onSuccess {
            intent { postSideEffect(FriendSideEffect.CopyInviteCode(it.code)) }
        }.onFailure {
            // TODO 에러 처리 어떻게 할지 논의 필요.
        }
    }

    override fun onAction(action: FriendAction) {
        when (action) {
            FriendAction.NavigateToBack -> intent { postSideEffect(FriendSideEffect.NavigateToBack) }
            is FriendAction.CopyInviteCode -> getTimeCapsuleInviteCode(action.capsuleId)
        }
    }
}

sealed interface FriendAction {
    data object NavigateToBack : FriendAction

    data class CopyInviteCode(val capsuleId: Long) : FriendAction
}

sealed interface FriendSideEffect {
    data object NavigateToBack : FriendSideEffect

    data class CopyInviteCode(val code: String) : FriendSideEffect
}