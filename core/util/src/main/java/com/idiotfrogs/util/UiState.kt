package com.idiotfrogs.util

sealed interface UiState<out T> {
    data object Init : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val errorMessage: String?) : UiState<Nothing>
}