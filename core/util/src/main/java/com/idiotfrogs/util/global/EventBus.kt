package com.idiotfrogs.util.global

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _appSideEffect = MutableSharedFlow<AppSideEffect>(extraBufferCapacity = 10)
    val appSideEffect = _appSideEffect.asSharedFlow()

    fun postSideEffect(appSideEffect: AppSideEffect) {
        _appSideEffect.tryEmit(appSideEffect)
    }
}