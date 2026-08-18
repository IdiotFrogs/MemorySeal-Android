package com.idiotfrogs.app_link

sealed interface AppLink {
    data class Invite(val capsuleId: Long) : AppLink
}
