package com.idiotfrogs.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Init)
    val uiState = _uiState
        .onStart {
            fetchInitUi()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SignUpUiState.Init
        )

    private val _event = MutableSharedFlow<SignUpEvent>()
    val event = _event.asSharedFlow()

    // TODO: 공통 error 처리 CEH 분리
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = SignUpUiState.Error(throwable.message)
    }

    private val safeScope = viewModelScope + coroutineExceptionHandler

    private fun fetchInitUi() {
        safeScope.launch {
            _uiState.emit(SignUpUiState.Success)
        }
    }

    fun signUp() {
        safeScope.launch {
//            delay(1000)
            _event.emit(SignUpEvent.NavigateToHome)
        }
    }
}

sealed interface SignUpUiState {
    data object Init : SignUpUiState
    data object Success : SignUpUiState
    data class Error(val errorMessage: String?) : SignUpUiState
}

sealed interface SignUpEvent {
    data object NavigateToHome : SignUpEvent
}