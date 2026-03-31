package com.idiotfrogs.detail

import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(

): BaseViewModel<DetailAction>() {
    private val _event = MutableSharedFlow<DetailEvent>()
    val event = _event.asSharedFlow()

    override fun onAction(action: DetailAction) {
        when (action) {
            is DetailAction.NavigateToFriend -> navigateToFriend(action.id)
            DetailAction.NavigateToBack -> navigateToBack()
        }
    }

    private fun navigateToFriend(id: Int) {
        safeLaunch { _event.emit(DetailEvent.NavigateToFriend(id)) }
    }

    private fun navigateToBack() {
        safeLaunch { _event.emit(DetailEvent.NavigateToBack) }
    }

}

sealed interface DetailAction {
    data class NavigateToFriend(val id: Int) : DetailAction
    data object NavigateToBack : DetailAction
}

sealed interface DetailEvent {
    data class NavigateToFriend(val id: Int): DetailEvent
    data object NavigateToBack : DetailEvent
}