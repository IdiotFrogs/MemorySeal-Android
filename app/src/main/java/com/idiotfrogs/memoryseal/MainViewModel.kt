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
    private val _event = MutableSharedFlow<MainEvent>(extraBufferCapacity = 1)
    val event = _event.asSharedFlow()

    fun collectAppSideEffect() {
        viewModelScope.launch {
            MSSideEffect.appSideEffect.collect { sideEffect ->
                when (sideEffect) {
                    AppSideEffect.LoginRequired -> _event.emit(MainEvent.NavigateToLogin)
                    is AppSideEffect.TimeCapsuleInviteLinkOpened -> {
                        // TODO 서버에서 capsuleId 기반 참여 API가 제공되면 Detail 이동 전에 참여 요청 UseCase를 호출한다.
                        _event.emit(MainEvent.NavigateToDetail(sideEffect.capsuleId))
                    }
                }
            }
        }
    }
}

sealed interface MainEvent {
    object NavigateToLogin : MainEvent
    data class NavigateToDetail(val capsuleId: Long) : MainEvent
}
