package com.idiotfrogs.util

sealed interface UiState {
    data object Init : UiState
    data class Success(val data: Any? = null) : UiState
    data class Error(val errorMessage: String?) : UiState
}