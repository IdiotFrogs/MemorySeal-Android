package com.idiotfrogs.model.timecapsule

import kotlinx.serialization.Serializable

@Serializable
data class PendingCollaboratorsRequest(
    val code: String,
)