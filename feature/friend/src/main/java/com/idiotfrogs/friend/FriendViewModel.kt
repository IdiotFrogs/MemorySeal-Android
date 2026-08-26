package com.idiotfrogs.friend

import androidx.compose.runtime.Immutable
import com.idiotfrogs.app_link.AppLinkManager
import com.idiotfrogs.domain.usecase.timecapsule.DelegationTimeCapsuleHostUseCase
import com.idiotfrogs.domain.usecase.timecapsule.DeleteTimeCapsuleContributorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleCollaboratorsUseCase
import com.idiotfrogs.domain.usecase.timecapsule.GetTimeCapsuleInviteCodeUseCase
import com.idiotfrogs.domain.usecase.timecapsule.SearchTimeCapsuleCollaboratorsUseCase
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponseData
import com.idiotfrogs.util.base.DataUiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.paging.PaginationState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container

private const val FRIEND_PAGE_SIZE = 20

@HiltViewModel(assistedFactory = FriendViewModel.Factory::class)
class FriendViewModel @AssistedInject constructor(
    @Assisted private val capsuleId: Long,
    private val appLinkManager: AppLinkManager,
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
        intent {
            reduce {
                state.copy(
                    data = CollaboratorsData(state.data?.collaborators?.clear() ?: PaginationState()),
                    isLoading = true,
                )
            }
        }

        getTimeCapsuleCollaboratorsUseCase(capsuleId, 0, FRIEND_PAGE_SIZE).onSuccess {
            intent {
                reduce {
                    state.copy(
                        data = CollaboratorsData(PaginationState<TimeCapsuleCollaboratorsResponseData>().addPage(it)),
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
                postSideEffect(FriendSideEffect.CopyInviteCodeToClipboard(it.code))
                postSideEffect(FriendSideEffect.ShowToast(FriendScreenActionState.COPY))
            }
        }.onFailure {
            intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
            // TODO 에러 처리 어떻게 할지 논의 필요.
        }
    }

    private fun shareInviteLink(capsuleId: Long) = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        appLinkManager.createInviteLink(capsuleId).onSuccess { inviteLink ->
            intent {
                reduce { state.copy(isLoading = false, errorMessage = null) }
                postSideEffect(FriendSideEffect.ShareInviteLink(inviteLink))
            }
        }.onFailure {
            intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
        }
    }

    private fun delegationTimeCapsuleHost(targetUserId: Long) = safeLaunch {
        intent { reduce { state.copy(isLoading = true) } }

        delegationTimeCapsuleHostUseCase(capsuleId, targetUserId).onSuccess {
            getTimeCapsuleCollaboratorsUseCase(capsuleId, 0, FRIEND_PAGE_SIZE).onSuccess {
                intent {
                    reduce {
                        state.copy(
                            data = CollaboratorsData(PaginationState<TimeCapsuleCollaboratorsResponseData>().addPage(it)),
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
            getTimeCapsuleCollaboratorsUseCase(capsuleId, 0, FRIEND_PAGE_SIZE).onSuccess {
                intent {
                    reduce {
                        state.copy(
                            data = CollaboratorsData(PaginationState<TimeCapsuleCollaboratorsResponseData>().addPage(it)),
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
        intent {
            reduce {
                state.copy(
                    data = CollaboratorsData(
                        collaborators = state.data?.collaborators?.clear() ?: PaginationState(),
                        searchKeyword = nickname,
                    ),
                    isLoading = true,
                )
            }
        }

        searchTimeCapsuleCollaboratorsUseCase(capsuleId, nickname, 0, FRIEND_PAGE_SIZE).onSuccess {
            intent {
                reduce {
                    state.copy(
                        data = CollaboratorsData(
                            collaborators = PaginationState<TimeCapsuleCollaboratorsResponseData>().addPage(it),
                            searchKeyword = nickname,
                        ),
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }
        }.onFailure {
            intent { reduce { reduceLoadingFailure(state, it.message) } }
        }
    }

    private fun fetchNextCollaboratorsPage() = intent {
        val collaboratorsData = state.data ?: return@intent
        val collaborators = collaboratorsData.collaborators

        if (state.isLoading || !collaborators.canLoadMore) return@intent

        val currentPage = collaborators.currentPage
        val nextPage = currentPage + 1
        val searchKeyword = collaboratorsData.searchKeyword

        reduce {
            state.copy(
                data = collaboratorsData.copy(
                    collaborators = collaborators.setLoadingMore(true),
                ),
            )
        }

        val result = if (searchKeyword == null) {
            getTimeCapsuleCollaboratorsUseCase(capsuleId, nextPage, FRIEND_PAGE_SIZE)
        } else {
            searchTimeCapsuleCollaboratorsUseCase(
                capsuleId,
                searchKeyword,
                nextPage,
                FRIEND_PAGE_SIZE,
            )
        }

        result.onSuccess { response ->
            val latestData = state.data ?: return@onSuccess
            val latestCollaborators = latestData.collaborators
            val isCurrentRequest = latestData.searchKeyword == searchKeyword &&
                latestCollaborators.currentPage == currentPage &&
                latestCollaborators.isLoadingMore

            if (!isCurrentRequest) return@onSuccess

            reduce {
                state.copy(
                    data = latestData.copy(
                        collaborators = latestCollaborators.addPage(response),
                    ),
                    errorMessage = null,
                )
            }
        }.onFailure {
            val latestData = state.data ?: return@onFailure
            val latestCollaborators = latestData.collaborators
            val isCurrentRequest = latestData.searchKeyword == searchKeyword &&
                latestCollaborators.currentPage == currentPage &&
                latestCollaborators.isLoadingMore

            if (!isCurrentRequest) return@onFailure

            reduce {
                state.copy(
                    data = latestData.copy(
                        collaborators = latestCollaborators.setLoadingMore(false),
                    ),
                    errorMessage = it.message,
                )
            }
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
            FriendAction.BackClicked -> intent { postSideEffect(FriendSideEffect.NavigateToBack) }
            is FriendAction.InviteLinkShareClicked -> shareInviteLink(action.capsuleId)
            is FriendAction.InviteCodeCopyClicked -> getTimeCapsuleInviteCode(action.capsuleId)
            is FriendAction.DelegationHostConfirmed -> delegationTimeCapsuleHost(action.targetUserId)
            is FriendAction.DeleteContributorConfirmed -> deleteTimeCapsuleContributor(action.targetUserId)
            is FriendAction.SearchSubmitted -> searchTimeCapsuleCollaborators(action.nickname)
            FriendAction.NextCollaboratorsPageRequested -> fetchNextCollaboratorsPage()
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
    val collaborators: PaginationState<TimeCapsuleCollaboratorsResponseData> = PaginationState(),
    val searchKeyword: String? = null,
)

sealed interface FriendAction {
    data object BackClicked : FriendAction
    data class InviteLinkShareClicked(val capsuleId: Long) : FriendAction
    data class InviteCodeCopyClicked(val capsuleId: Long) : FriendAction
    data class DelegationHostConfirmed(val targetUserId: Long) : FriendAction
    data class DeleteContributorConfirmed(val targetUserId: Long) : FriendAction
    data class SearchSubmitted(val nickname: String) : FriendAction
    data object NextCollaboratorsPageRequested : FriendAction
}

sealed interface FriendSideEffect {
    data object NavigateToBack : FriendSideEffect
    data class CopyInviteCodeToClipboard(val code: String) : FriendSideEffect
    data class ShareInviteLink(val inviteLink: String) : FriendSideEffect
    data class ShowToast(val state: FriendScreenActionState) : FriendSideEffect
}

private fun PaginationState<TimeCapsuleCollaboratorsResponseData>.addPage(
    response: TimeCapsuleCollaboratorsResponse,
): PaginationState<TimeCapsuleCollaboratorsResponseData> = addPage(
    newItems = response.content,
    page = response.number,
    totalElements = response.totalElements.toLong(),
    isLast = response.last,
)
