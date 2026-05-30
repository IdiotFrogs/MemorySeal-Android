package com.idiotfrogs.model.timecapsule

import kotlinx.serialization.Serializable

@Serializable
data class ProcessCollaboratorRequest(
    val isApproved: Boolean,
)