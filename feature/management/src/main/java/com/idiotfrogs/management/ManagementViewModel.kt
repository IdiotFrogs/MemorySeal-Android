package com.idiotfrogs.management

import com.idiotfrogs.domain.usecase.timecapsule.DeleteTimeCapsuleUseCase
import com.idiotfrogs.domain.usecase.timecapsule.LeaveTimeCapsuleUseCase
import com.idiotfrogs.util.UiState
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
) : BaseViewModel<UiState<Unit>, ManagementSideEffect, ManagementAction>() {

    override val container: Container<UiState<Unit>, ManagementSideEffect> =
        container(UiState.Success(Unit))

    override fun onAction(action: ManagementAction) {
        when (action) {
            ManagementAction.NavigateToBack -> intent { postSideEffect(ManagementSideEffect.NavigateToBack) }
            is ManagementAction.DeleteCapsule -> deleteTimeCapsule(action.capsuleId)
            is ManagementAction.LeaveTimeCapsule -> leaveTimeCapsule(action.capsuleId)
            ManagementAction.NavigateToFriend -> intent { postSideEffect(ManagementSideEffect.NavigateToFriend) }
        }
    }

    private fun deleteTimeCapsule(capsuleId: Long) {
        safeLaunch {
            deleteTimeCapsuleUseCase(capsuleId).onSuccess {
                RefreshSideEffect.tryEmit(RefreshEvent.Home)
                intent { postSideEffect(ManagementSideEffect.NavigateToHome) }
            }.onFailure {
                // TODO 에러 Toast 작업?
            }
        }
    }

    private fun leaveTimeCapsule(capsuleId: Long) {
        safeLaunch {
            leaveTimeCapsuleUseCase(capsuleId).onSuccess {
                RefreshSideEffect.tryEmit(RefreshEvent.Home)
                intent { postSideEffect(ManagementSideEffect.NavigateToHome) }
            }.onFailure {
                // TODO 에러 dialog 작업
            }
        }
    }
}

sealed interface ManagementAction {
    data object NavigateToBack : ManagementAction
    data object NavigateToFriend : ManagementAction

    data class DeleteCapsule(val capsuleId: Long) : ManagementAction
    data class LeaveTimeCapsule(val capsuleId: Long) : ManagementAction
}

sealed interface ManagementSideEffect {
    data object NavigateToHome : ManagementSideEffect
    data object NavigateToBack : ManagementSideEffect
    data object NavigateToFriend : ManagementSideEffect
}