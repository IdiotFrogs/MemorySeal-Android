package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class WateringUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend fun invoke(capsuleId: Long) = safeCatching {
        timeCapsuleRepository.watering(capsuleId)
    }
}