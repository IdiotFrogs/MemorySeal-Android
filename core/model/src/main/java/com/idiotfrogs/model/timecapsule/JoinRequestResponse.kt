package com.idiotfrogs.model.timecapsule

import kotlinx.serialization.Serializable

@Serializable
data class JoinRequestResponse(
    val capsuleId: Long,
)
