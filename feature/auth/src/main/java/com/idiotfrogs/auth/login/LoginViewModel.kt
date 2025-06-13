package com.idiotfrogs.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idiotfrogs.domain.exception.LoginCancelledException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
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
class LoginViewModel @Inject constructor(): ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Init)
    val uiState = _uiState
        .onStart {
            fetchInitUi()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            LoginUiState.Init
        )

    private val _event = MutableSharedFlow<LoginEvent>()
    val event = _event.asSharedFlow()

    // TODO: 공통 error 처리 CEH 분리
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable !is LoginCancelledException) {
            _uiState.value = LoginUiState.Error(throwable.message)
        }
    }

    private val safeScope = viewModelScope + coroutineExceptionHandler

    fun fetchInitUi() {
        safeScope.launch {
            _uiState.emit(LoginUiState.Success)
        }
    }

    fun socialLogin(loginCallback: suspend () -> Unit) {
        safeScope.launch {
            loginCallback()
            _event.emit(LoginEvent.NavigateToSignUp)
        }
    }
}

sealed interface LoginUiState {
    data object Init : LoginUiState
    data object Success : LoginUiState
    data class Error(val errorMessage: String?) : LoginUiState
}

sealed interface LoginEvent {
    data object NavigateToSignUp : LoginEvent
}