package com.idiotfrogs.message

import com.idiotfrogs.util.base.BaseUiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor() :
    BaseViewModel<MessageUiState, MessageSideEffect, MessageAction>() {

    override val container: Container<MessageUiState, MessageSideEffect> =
        container(MessageUiState())

    override fun onAction(action: MessageAction) {
        when (action) {
            MessageAction.NavigateToBack -> intent {
                postSideEffect(MessageSideEffect.NavigateToBack)
            }
        }
    }
}

data class MessageUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState

sealed interface MessageAction {
    data object NavigateToBack : MessageAction
}

sealed interface MessageSideEffect {
    data object NavigateToBack : MessageSideEffect
}
