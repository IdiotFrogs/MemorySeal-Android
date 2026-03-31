package com.idiotfrogs.home

import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

): BaseViewModel<HomeAction>() {
    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    override fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.NavigateToCreate -> navigateToCreate()
            HomeAction.NavigateToProfile -> navigateToProfile()
            is HomeAction.NavigateToDetail -> navigateToDetail(action.id)
        }
    }

    private fun navigateToCreate() {
        safeLaunch { _event.emit(HomeEvent.NavigateToCreate) }
    }

    private fun navigateToProfile() {
        safeLaunch { _event.emit(HomeEvent.NavigateToProfile) }
    }

    private fun navigateToDetail(id: Long) {
        safeLaunch { _event.emit(HomeEvent.NavigateToDetail(id)) }
    }

}

sealed interface HomeAction {
    data object NavigateToCreate : HomeAction
    data object NavigateToProfile : HomeAction
    data class NavigateToDetail(val id: Long) : HomeAction
}

sealed interface HomeEvent {
    data object NavigateToCreate : HomeEvent
    data object NavigateToProfile : HomeEvent
    data class NavigateToDetail(val id: Long) : HomeEvent
}