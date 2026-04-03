package com.idiotfrogs.friend

import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(

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

    override fun onAction(action: FriendAction) {
        when (action) {
            FriendAction.NavigateToBack -> intent { postSideEffect(FriendSideEffect.NavigateToBack) }
        }
    }
}

sealed interface FriendAction {
    data object NavigateToBack : FriendAction
}

sealed interface FriendSideEffect {
    data object NavigateToBack : FriendSideEffect
}