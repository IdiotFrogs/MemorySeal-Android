package com.idiotfrogs.auth.signup

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.idiotfrogs.domain.usecase.user.SignUpUseCase
import com.idiotfrogs.util.UiState
import com.idiotfrogs.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel<SignUpAction>() {
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

    override fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.SignUp -> signUp(action.nickname, action.file)
            SignUpAction.NavigateToBack -> navigateToBack()
        }
    }

    private fun fetchInitUi() {
        safeLaunch {
            _uiState.emit(UiState.Init)
            // TODO UI 로딩에 필요한 작업
            _uiState.emit(UiState.Success)
        }
    }

    private fun signUp(nickname: String, file: File?) {
        Log.d("CHSCHS", "nickname: $nickname, file: ${file?.name}")
        if (file == null) return

        safeLaunch {
            runCatching { signUpUseCase(nickname, file) }
                .onSuccess { _event.emit(SignUpEvent.NavigateToHome) }
                .onFailure {
                    Log.d("CHSCHS", it.message.toString())
                }
        }
    }

    private fun navigateToBack() {
        safeLaunch { _event.emit(SignUpEvent.NavigateToBack) }
    }
}

sealed interface SignUpAction {
    data class SignUp(val nickname: String, val file: File?): SignUpAction
    data object NavigateToBack : SignUpAction
}

sealed interface SignUpEvent {
    data object NavigateToHome : SignUpEvent
    data object NavigateToBack : SignUpEvent
}