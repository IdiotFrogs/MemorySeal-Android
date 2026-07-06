package com.idiotfrogs.util.sideEffect

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object MSSideEffect {
    private val _appSideEffect = Channel<AppSideEffect>(Channel.BUFFERED)
    val appSideEffect = _appSideEffect.receiveAsFlow()

    fun postSideEffect(appSideEffect: AppSideEffect) {
        _appSideEffect.trySend(appSideEffect)
    }
}