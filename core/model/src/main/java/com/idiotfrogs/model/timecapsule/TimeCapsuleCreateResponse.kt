package com.idiotfrogs.model.timecapsule

import kotlinx.serialization.Serializable

@Serializable
data class TimeCapsuleCreateResponse(
    val id: Long,
    val title: String,
    val description: String,
    val openedAt: String, // TODO 실제로 넣는 Date 타입을 파싱해야합니다.
    val timeCapsuleStatus: String,
    val mainImageUrl: String
)