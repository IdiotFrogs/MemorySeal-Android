package com.idiotfrogs.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idiotfrogs.data.SampleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    // sampleUsecase
    private val sampleManager: SampleManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<SampleUiState>(SampleUiState.Init)
    val uiState = _uiState
        .onStart {
            fetchInitUi()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SampleUiState.Init
        )

    private fun fetchInitUi() {
        viewModelScope.launch {
            _uiState.emit(SampleUiState.Init)
            /**
                sampleUsecase.onSuccess {
                    _uiState.emit(SampleUiState.Success)
                }.onFailure {
                    _uiState.emit(SampleUiState.Error)
                }
             */
        }
    }

    fun googleLogin() {
        // 추가 구현 필요
        sampleManager.googleLogin()
    }

    fun appleLogin() {
        // 추가 구현 필요
        sampleManager.appleLogin()
    }
}

sealed interface SampleUiState {
    data object Init : SampleUiState
    data object Success : SampleUiState
    data class Error(val errorMessage: String?) : SampleUiState
}