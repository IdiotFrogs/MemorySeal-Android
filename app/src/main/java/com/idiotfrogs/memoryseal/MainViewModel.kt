package com.idiotfrogs.memoryseal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idiotfrogs.util.sideEffect.AppSideEffect
import com.idiotfrogs.util.sideEffect.MSSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {
    private val _event = MutableSharedFlow<MainEvent>()
    val event = _event.asSharedFlow()

    init {
        collectAppSideEffect()
    }

    private fun collectAppSideEffect() {
        viewModelScope.launch {
            MSSideEffect.appSideEffect.collect { sideEffect ->
                when (sideEffect) {
                    AppSideEffect.LoginRequired -> _event.emit(MainEvent.NavigateToLogin)
                }
            }
        }
    }
}

sealed interface MainEvent {
    object NavigateToLogin : MainEvent
}