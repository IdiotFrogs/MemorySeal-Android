package com.idiotfrogs.friend

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.DelegationTimeCapsuleHostUseCase
import com.idiotfrogs.domain.usecase.timecapsule.DeleteTimeCapsuleContributorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleCollaboratorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleInviteCodeUseCase
import com.idiotfrogs.domain.usecase.timecapsule.SearchTimeCapsuleCollaboratorsUseCase
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
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
    private val getTimeCapsuleCollaboratorsUseCase: GetTimeCapsuleCollaboratorsUseCase,
    private val delegationTimeCapsuleHostUseCase: DelegationTimeCapsuleHostUseCase,
    private val deleteTimeCapsuleContributorsUseCase: DeleteTimeCapsuleContributorsUseCase,
    private val searchTimeCapsuleCollaboratorsUseCase: SearchTimeCapsuleCollaboratorsUseCase,
) : BaseViewModel<UiState<CollaboratorsData>, FriendSideEffect, FriendAction>() {

    override val container: Container<UiState<CollaboratorsData>, FriendSideEffect> = container(
        initialState = UiState.Init,
        onCreate = { fetchFriend() }
    )

    private fun fetchFriend() = safeLaunch {
        getTimeCapsuleCollaboratorsUseCase(capsuleId, 0, 20).onSuccess {
            intent {
                reduce {
                    UiState.Success(CollaboratorsData(collaborators = it))
                }
            }
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

    private fun delegationTimeCapsuleHost(targetUserId: Long) = safeLaunch {
        delegationTimeCapsuleHostUseCase(capsuleId, targetUserId).onSuccess {
            fetchFriend()
        }.onFailure {
            intent { reduce { UiState.Error(it.message) } }
        }
    }

    private fun deleteTimeCapsuleContributor(targetUserId: Long) = safeLaunch {
        deleteTimeCapsuleContributorsUseCase(capsuleId, targetUserId).onSuccess {
            fetchFriend()
        }.onFailure {
            intent { reduce { UiState.Error(it.message) } }
        }
    }

    private fun searchTimeCapsuleCollaborators(nickname: String) = safeLaunch {
        searchTimeCapsuleCollaboratorsUseCase(capsuleId, nickname, 0, 20).onSuccess {
            intent {
                reduce {
                    UiState.Success(CollaboratorsData(collaborators = it))
                }
            }
        }.onFailure {
            intent { reduce { UiState.Error(it.message) } }
        }
    }

    override fun onAction(action: FriendAction) {
        when (action) {
            FriendAction.NavigateToBack -> intent { postSideEffect(FriendSideEffect.NavigateToBack) }
            is FriendAction.CopyInviteCode -> getTimeCapsuleInviteCode(action.capsuleId)
            is FriendAction.DelegationHost -> delegationTimeCapsuleHost(action.targetUserId)
            is FriendAction.DeleteContributor -> deleteTimeCapsuleContributor(action.targetUserId)
            is FriendAction.SearchCollaborators -> searchTimeCapsuleCollaborators(action.nickname)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(capsuleId: Long): FriendViewModel
    }
}

@Immutable
data class CollaboratorsData(
    val collaborators: TimeCapsuleCollaboratorsResponse? = null,
)

sealed interface FriendAction {
    data object NavigateToBack : FriendAction

    data class CopyInviteCode(val capsuleId: Long) : FriendAction

    data class DelegationHost(val targetUserId: Long) : FriendAction

    data class DeleteContributor(val targetUserId: Long) : FriendAction

    data class SearchCollaborators(val nickname: String) : FriendAction
}

sealed interface FriendSideEffect {
    data object NavigateToBack : FriendSideEffect

    data class CopyInviteCode(val code: String) : FriendSideEffect

    data class ShowToast(val state: FriendScreenActionState) : FriendSideEffect
}
