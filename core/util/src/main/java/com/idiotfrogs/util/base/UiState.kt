package com.idiotfrogs.util.base

interface BaseUiState {
    val isLoading: Boolean
}

interface DataUiState<T> : BaseUiState {
    val data: T?
}
