package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.BuryTimeCapsuleRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class BuryTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(
        capsuleId: Long,
        body: BuryTimeCapsuleRequest
    ): Result<TimeCapsuleResponse> = safeCatching {
        timeCapsuleRepository.buryTimeCapsule(capsuleId, body)
    }
}