package com.idiotfrogs.message

import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor() :
    BaseViewModel<UiState<Unit>, MessageSideEffect, MessageAction>() {

    override val container: Container<UiState<Unit>, MessageSideEffect> =
        container(UiState.Success(Unit))

    override fun onAction(action: MessageAction) {
        when (action) {
            MessageAction.NavigateToBack -> intent {
                postSideEffect(MessageSideEffect.NavigateToBack)
            }
        }
    }
}

sealed interface MessageAction {
    data object NavigateToBack : MessageAction
}

sealed interface MessageSideEffect {
    data object NavigateToBack : MessageSideEffect
}
