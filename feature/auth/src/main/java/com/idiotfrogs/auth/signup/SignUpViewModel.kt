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
) : BaseViewModel() {
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

    fun signUp(nickname: String, file: File?) {
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
}

sealed interface SignUpEvent {
    data object NavigateToHome : SignUpEvent
}