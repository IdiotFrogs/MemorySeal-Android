package com.idiotfrogs.util.sideEffect

sealed interface AppSideEffect {
    object LoginRequired : AppSideEffect
    data class TimeCapsuleInviteLinkOpened(val capsuleId: Long) : AppSideEffect
}