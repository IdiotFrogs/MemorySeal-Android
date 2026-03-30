package com.idiotfrogs.home

import androidx.lifecycle.viewModelScope
import com.idiotfrogs.domain.usecase.timecapsule.GetMyTimeCapsuleUseCase
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMyTimeCapsuleUseCase: GetMyTimeCapsuleUseCase
): BaseViewModel<HomeAction>() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Init)
    val uiState = _uiState
        .onStart {
            fetchInitUi()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Init
        )


    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    private fun fetchInitUi() {
        safeLaunch {
            val result = getMyTimeCapsuleUseCase()
            _uiState.update { UiState.Success(data = result) }
        }
    }

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