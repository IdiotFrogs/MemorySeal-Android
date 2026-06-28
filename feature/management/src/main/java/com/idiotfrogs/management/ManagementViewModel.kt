package com.idiotfrogs.management

import com.idiotfrogs.domain.usecase.timecapsule.DeleteTimeCapsuleUseCase
import com.idiotfrogs.domain.usecase.timecapsule.LeaveTimeCapsuleUseCase
import com.idiotfrogs.util.base.BaseUiState
import com.idiotfrogs.util.base.BaseViewModel
import com.idiotfrogs.util.sideEffect.RefreshEvent
import com.idiotfrogs.util.sideEffect.RefreshSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ManagementViewModel @Inject constructor(
    private val deleteTimeCapsuleUseCase: DeleteTimeCapsuleUseCase,
    private val leaveTimeCapsuleUseCase: LeaveTimeCapsuleUseCase,
) : BaseViewModel<ManagementUiState, ManagementSideEffect, ManagementAction>() {

    override val container: Container<ManagementUiState, ManagementSideEffect> =
        container(ManagementUiState())

    override fun onAction(action: ManagementAction) {
        when (action) {
            ManagementAction.BackClicked -> intent { postSideEffect(ManagementSideEffect.NavigateToBack) }
            is ManagementAction.DeleteCapsuleConfirmed -> deleteTimeCapsule(action.capsuleId)
            is ManagementAction.LeaveCapsuleConfirmed -> leaveTimeCapsule(action.capsuleId)
            ManagementAction.MemberClicked -> intent { postSideEffect(ManagementSideEffect.NavigateToFriend) }
        }
    }

    private fun deleteTimeCapsule(capsuleId: Long) {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            deleteTimeCapsuleUseCase(capsuleId).onSuccess {
                intent { reduce { state.copy(isLoading = false, errorMessage = null) } }
                RefreshSideEffect.tryEmit(RefreshEvent.Home)
                intent { postSideEffect(ManagementSideEffect.NavigateToHome) }
            }.onFailure {
                intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
                // TODO 에러 Toast 작업?
            }
        }
    }

    private fun leaveTimeCapsule(capsuleId: Long) {
        safeLaunch {
            intent { reduce { state.copy(isLoading = true) } }

            leaveTimeCapsuleUseCase(capsuleId).onSuccess {
                intent { reduce { state.copy(isLoading = false, errorMessage = null) } }
                RefreshSideEffect.tryEmit(RefreshEvent.Home)
                intent { postSideEffect(ManagementSideEffect.NavigateToHome) }
            }.onFailure {
                intent { reduce { state.copy(isLoading = false, errorMessage = it.message) } }
                // TODO 에러 dialog 작업
            }
        }
    }
}

data class ManagementUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState

sealed interface ManagementAction {
    data object BackClicked : ManagementAction
    data object MemberClicked : ManagementAction
    data class DeleteCapsuleConfirmed(val capsuleId: Long) : ManagementAction
    data class LeaveCapsuleConfirmed(val capsuleId: Long) : ManagementAction
}

sealed interface ManagementSideEffect {
    data object NavigateToHome : ManagementSideEffect
    data object NavigateToBack : ManagementSideEffect
    data object NavigateToFriend : ManagementSideEffect
}
