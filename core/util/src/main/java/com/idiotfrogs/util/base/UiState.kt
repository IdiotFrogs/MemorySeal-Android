package com.idiotfrogs.util.base

interface BaseUiState {
    val isLoading: Boolean
    val errorMessage: String?
}

interface DataUiState<T> : BaseUiState {
    val data: T?
}
