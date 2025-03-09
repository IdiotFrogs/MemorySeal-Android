package com.idiotfrogs.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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

    // TODO: 공통 error 처리 CEH 분리
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = LoginUiState.Error(throwable.message)
    }

    private val safeScope = viewModelScope + coroutineExceptionHandler

    private fun fetchInitUi() {
        safeScope.launch {
            _uiState.emit(LoginUiState.UiLoaded)
        }
    }

    fun socialLogin(loginCallback: suspend () -> Unit) {
        safeScope.launch {
            loginCallback()
            _uiState.emit(LoginUiState.LoginSuccess)
        }
    }
}

sealed interface LoginUiState {
    data object Init : LoginUiState
    data object UiLoaded : LoginUiState
    data object LoginSuccess: LoginUiState
    data class Error(val errorMessage: String?) : LoginUiState
}