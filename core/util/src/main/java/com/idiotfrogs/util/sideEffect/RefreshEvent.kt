package com.idiotfrogs.util.sideEffect

sealed interface RefreshEvent {
    data object Home : RefreshEvent
    data object Profile : RefreshEvent
    data class Detail(val id: Long) : RefreshEvent
}