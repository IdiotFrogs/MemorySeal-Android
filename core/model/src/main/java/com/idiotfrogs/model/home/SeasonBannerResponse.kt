package com.idiotfrogs.model.home

import kotlinx.serialization.Serializable

/** 없을경우 빈 {}로 와서 기본 값 지정이 안되어 있으면 예외 발생 */
@Serializable
data class SeasonBannerResponse(
    val content: String = "",
    val season: Season? = null,
    val capsuleId: Long? = null,
)

enum class Season { SPRING, SUMMER, FALL, WINTER }