package com.idiotfrogs.auth.signup

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Init)
    val uiState = _uiState
        .onStart {
            fetchInitUi()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SignUpUiState.Init
        )

    val textFieldState = TextFieldState()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    suspend fun validateText() {
        snapshotFlow { textFieldState.text }
            .debounce(300)
            .onEach { text ->
                val validationType = validate(text)

                validationType?.let {
                    _errorMessage.value = it.errorMessage
                } ?: run {
                    _errorMessage.value = null
                }
            }
    }

    private fun validate(text: CharSequence): ValidationType? {
        if (text.isEmpty()) return ValidationType.Empty // FIXME: 한 글자일 때 처리
        if (text.first().isWhitespace() || text.last().isWhitespace()) ValidationType.FirstOrLastWhiteSpace
        if (text.all { it.isWhitespace() }) ValidationType.AllWhiteSpace
        if (text.contains("  ")) ValidationType.RepeatedWhiteSpace
        if (text.length !in 1..16) ValidationType.LengthOutOfRange
        val blankSpecialChar = Regex("[\u00A0\u1680\u2000-\u200A\u202F\u205F\u3000\u180E\u200B\u200C\u200D\u200E\u200F\u2060\uFEFF\u202A-\u202E]")
        if (blankSpecialChar.containsMatchIn(text)) ValidationType.InvalidChar
        if (false) ValidationType.Duplicated // TODO: duplication check
        return null
    }

    // TODO: 공통 error 처리 CEH 분리
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = SignUpUiState.Error(throwable.message)
    }

    private val safeScope = viewModelScope + coroutineExceptionHandler

    private fun fetchInitUi() {
        safeScope.launch {
            _uiState.emit(SignUpUiState.UiLoaded)
        }
    }

    fun signUp() {
        safeScope.launch {
            delay(1000)
            _uiState.emit(SignUpUiState.SignUpSuccess)
        }
    }
}

sealed interface SignUpUiState {
    data object Init : SignUpUiState
    data object UiLoaded : SignUpUiState
    data object SignUpSuccess : SignUpUiState
    data class Error(val errorMessage: String?) : SignUpUiState
}