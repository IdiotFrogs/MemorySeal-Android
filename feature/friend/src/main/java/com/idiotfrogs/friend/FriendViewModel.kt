package com.idiotfrogs.friend

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.GetRequestCollaboratorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleInviteCodeUseCase
import com.idiotfrogs.domain.usecase.timecapsule.ProcessRequestUseCase
import com.idiotfrogs.model.timecapsule.ProcessCollaboratorRequest
import com.idiotfrogs.model.timecapsule.RequestCollaboratorsResponse
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel(assistedFactory = FriendViewModel.Factory::class)
class FriendViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val getTimeCapsuleInviteCodeUseCase: GetTimeCapsuleInviteCodeUseCase,
    private val processRequestUseCase: ProcessRequestUseCase,
    private val getRequestCollaboratorsUseCase: GetRequestCollaboratorsUseCase,
) : BaseViewModel<UiState<CollaboratorsData>, FriendSideEffect, FriendAction>() {

    override val container: Container<UiState<CollaboratorsData>, FriendSideEffect> = container(
        initialState = UiState.Init,
        onCreate = { fetchFriend() }
    )

    private fun fetchFriend() = safeLaunch {
        getRequestCollaboratorsUseCase(capsuleId).onSuccess {
            intent { reduce { UiState.Success(CollaboratorsData(it)) } }
        }.onFailure {
            intent { reduce { UiState.Error(it.message) } }
        }
    }

    private fun getTimeCapsuleInviteCode(capsuleId: Long) = safeLaunch {
        getTimeCapsuleInviteCodeUseCase(capsuleId).onSuccess {
            intent {
                postSideEffect(FriendSideEffect.CopyInviteCode(it.code))
                postSideEffect(FriendSideEffect.ShowToast(FriendScreenActionState.COPY))
            }
        }.onFailure {
            // TODO 에러 처리 어떻게 할지 논의 필요.
        }
    }

    private fun processRequest(requestId: Long, body: ProcessCollaboratorRequest) = safeLaunch {
        processRequestUseCase(requestId, body).onSuccess {
            intent {
                postSideEffect(
                    FriendSideEffect.ShowToast(
                        if (body.isApproved) FriendScreenActionState.ACCEPT
                        else FriendScreenActionState.REJECT
                    )
                )
            }
            fetchFriend()
            RefreshSideEffect.tryEmit(RefreshEvent.Detail(capsuleId))
        }.onFailure {
            // TODO 에러 처리 어떻게 할지 논의 필요.
        }
    }

    override fun onAction(action: FriendAction) {
        when (action) {
            FriendAction.NavigateToBack -> intent { postSideEffect(FriendSideEffect.NavigateToBack) }
            is FriendAction.CopyInviteCode -> getTimeCapsuleInviteCode(action.capsuleId)
            is FriendAction.ProcessRequest -> processRequest(action.requestId, ProcessCollaboratorRequest(action.isApproved))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): FriendViewModel
    }
}

@Immutable
data class CollaboratorsData(
    val collaborators: List<RequestCollaboratorsResponse> = emptyList()
)

sealed interface FriendAction {
    data object NavigateToBack : FriendAction

    data class CopyInviteCode(val capsuleId: Long) : FriendAction

    data class ProcessRequest(val requestId: Long, val isApproved: Boolean) : FriendAction
}

sealed interface FriendSideEffect {
    data object NavigateToBack : FriendSideEffect

    data class CopyInviteCode(val code: String) : FriendSideEffect

    data class ShowToast(val state: FriendScreenActionState) : FriendSideEffect
}