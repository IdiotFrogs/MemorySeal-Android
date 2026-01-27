package com.idiotfrogs.util.sideEffect

sealed interface AppSideEffect {
    object LoginRequired : AppSideEffect
}