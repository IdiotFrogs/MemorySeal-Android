package com.idiotfrogs.auth.login

import androidx.lifecycle.viewModelScope
import com.idiotfrogs.domain.usecase.user.GetMyProfileUseCase
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
class LoginViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase
): BaseViewModel<LoginAction>() {
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

    private val _event = MutableSharedFlow<LoginEvent>()
    val event = _event.asSharedFlow()

    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.SocialLogin -> socialLogin(action.loginCallback)
        }
    }

    private fun fetchInitUi() {
        safeLaunch {
            _uiState.emit(UiState.Init)
            // TODO UI 로딩에 필요한 작업
            _uiState.emit(UiState.Success())
        }
    }

    private fun socialLogin(loginCallback: suspend () -> Unit) {
        safeLaunch {
            loginCallback()
            val isOnboarding = getMyProfileUseCase().isOnboarding
            if (isOnboarding) {
                _event.emit(LoginEvent.NavigateToHome)
            } else {
                _event.emit(LoginEvent.NavigateToSignUp)
            }
        }
    }
}

sealed interface LoginAction {
    data class SocialLogin(val loginCallback: suspend () -> Unit): LoginAction
}

sealed interface LoginEvent {
    data object NavigateToSignUp : LoginEvent
    data object NavigateToHome : LoginEvent
}