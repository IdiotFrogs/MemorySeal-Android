package com.idiotfrogs.util

sealed interface UiState {
    data object Init : UiState
    data object Success : UiState
    data class Error(val errorMessage: String?) : UiState
}