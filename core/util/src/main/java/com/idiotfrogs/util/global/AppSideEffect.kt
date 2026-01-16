package com.idiotfrogs.util.global

sealed interface AppSideEffect {
    object LoginRequired : AppSideEffect
}