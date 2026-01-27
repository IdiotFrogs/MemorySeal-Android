package com.idiotfrogs.auth.signup

import androidx.lifecycle.viewModelScope
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : BaseViewModel() {
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

    private val _event = MutableSharedFlow<SignUpEvent>()
    val event = _event.asSharedFlow()

    // TODO: 공통 error 처리 CEH 분리
    private fun fetchInitUi() {
        safeLaunch {
            _uiState.emit(UiState.Init)
            // TODO UI 로딩에 필요한 작업
            _uiState.emit(UiState.Success)
        }
    }

    fun signUp() {
        safeLaunch {
            _event.emit(SignUpEvent.NavigateToHome)
        }
    }
}

sealed interface SignUpEvent {
    data object NavigateToHome : SignUpEvent
}