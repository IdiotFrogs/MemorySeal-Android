package com.idiotfrogs.util.sideEffect

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object RefreshSideEffect {
    private val _events = MutableSharedFlow<RefreshEvent>(
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events = _events.asSharedFlow()

    fun tryEmit(event: RefreshEvent): Boolean = _events.tryEmit(event)
}