package com.idiotfrogs.app_link

sealed interface JoinInviteResult {
    data object Joined : JoinInviteResult
    data object AlreadyJoined : JoinInviteResult
}
