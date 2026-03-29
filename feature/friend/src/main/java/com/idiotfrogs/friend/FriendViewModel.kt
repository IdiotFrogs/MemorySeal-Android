package com.idiotfrogs.friend

import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(

): BaseViewModel<FriendAction>() {
    private val _event = MutableSharedFlow<FriendEvent>()
    val event = _event.asSharedFlow()

    override fun onAction(action: FriendAction) {
        when (action) {
            FriendAction.NavigateToBack -> navigateToBack()
        }
    }

    private fun navigateToBack() {
        safeLaunch { _event.emit(FriendEvent.NavigateToBack) }
    }
}

sealed interface FriendAction {
    data object NavigateToBack : FriendAction
}

sealed interface FriendEvent {
    data object NavigateToBack : FriendEvent
}