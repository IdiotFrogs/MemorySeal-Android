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

    private val _pushEvent = MutableSharedFlow<PushEvent>(replay = 1)
    val pushEvent = _pushEvent.asSharedFlow()

    fun collectAppSideEffect() {
        viewModelScope.launch {
            MSSideEffect.appSideEffect.collect { sideEffect ->
                when (sideEffect) {
                    AppSideEffect.LoginRequired -> _event.emit(MainEvent.NavigateToLogin)
                }
            }
        }
    }

    fun onPushReceived(
        type: String?,
        capsuleId: String?,
    ) {
        val id = capsuleId?.toLongOrNull() ?: return

        val event = when (type) {
            "member" -> PushEvent.NavigateToFriend(id)
            "detail" -> PushEvent.NavigateToDetail(id)
            "open" -> PushEvent.NavigateToPreview(id)
            else -> return
        }

        viewModelScope.launch { _pushEvent.emit(event) }
    }
}

sealed interface MainEvent {
    object NavigateToLogin : MainEvent
}

sealed interface PushEvent {
    data class NavigateToFriend(val capsuleId: Long) : PushEvent
    data class NavigateToDetail(val capsuleId: Long) : PushEvent
    data class NavigateToPreview(val capsuleId: Long) : PushEvent
}