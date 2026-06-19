package com.idiotfrogs.friend

import androidx.compose.runtime.Immutable
import com.idiotfrogs.domain.usecase.timecapsule.DelegationTimeCapsuleHostUseCase
import com.idiotfrogs.domain.usecase.timecapsule.DeleteTimeCapsuleContributorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleCollaboratorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleInviteCodeUseCase
import com.idiotfrogs.domain.usecase.timecapsule.SearchTimeCapsuleCollaboratorsUseCase
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.util.base.DataUiState
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
) : BaseViewModel<FriendUiState, FriendSideEffect, FriendAction>() {

    override val container: Container<FriendUiState, FriendSideEffect> = container(
        initialState = FriendUiState(),
        onCreate = { fetchFriend() }
    )

    private fun fetchFriend() = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        getTimeCapsuleCollaboratorsUseCase(capsuleId, 0, 20).onSuccess {
            intent {
                reduce {
                    state.copy(
                        data = CollaboratorsData(collaborators = it),
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }
        }.onFailure {
            intent { reduce { reduceLoadingFailure(state, it.message) } }
        }
    }

    private fun getTimeCapsuleInviteCode(capsuleId: Long) = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        getTimeCapsuleInviteCodeUseCase(capsuleId).onSuccess {
            intent {
                reduce { state.copy(isLoading = false, errorMessage = null) }
                postSideEffect(FriendSideEffect.CopyInviteCode(it.code))
                postSideEffect(FriendSideEffect.ShowToast(FriendScreenActionState.COPY))
            }
        }.onFailure {
            intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
            // TODO 에러 처리 어떻게 할지 논의 필요.
        }
    }

    private fun delegationTimeCapsuleHost(targetUserId: Long) = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        delegationTimeCapsuleHostUseCase(capsuleId, targetUserId).onSuccess {
            getTimeCapsuleCollaboratorsUseCase(capsuleId, 0, 20).onSuccess {
                intent {
                    reduce {
                        state.copy(
                            data = CollaboratorsData(collaborators = it),
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
            }.onFailure {
                intent { reduce { reduceLoadingFailure(state, it.message) } }
            }
        }.onFailure {
            intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
        }
    }

    private fun deleteTimeCapsuleContributor(targetUserId: Long) = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        deleteTimeCapsuleContributorsUseCase(capsuleId, targetUserId).onSuccess {
            getTimeCapsuleCollaboratorsUseCase(capsuleId, 0, 20).onSuccess {
                intent {
                    reduce {
                        state.copy(
                            data = CollaboratorsData(collaborators = it),
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
            }.onFailure {
                intent { reduce { reduceLoadingFailure(state, it.message) } }
            }
        }.onFailure {
            intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
        }
    }

    private fun searchTimeCapsuleCollaborators(nickname: String) = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        searchTimeCapsuleCollaboratorsUseCase(capsuleId, nickname, 0, 20).onSuccess {
            intent {
                reduce {
                    state.copy(
                        data = CollaboratorsData(collaborators = it),
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }
        }.onFailure {
            intent { reduce { reduceLoadingFailure(state, it.message) } }
        }
    }

    private fun reduceLoadingFailure(
        currentState: FriendUiState,
        errorMessage: String?,
    ): FriendUiState {
        return currentState.copy(
            isLoading = false,
            errorMessage = errorMessage,
        )
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
data class FriendUiState(
    override val data: CollaboratorsData? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : DataUiState<CollaboratorsData>

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
