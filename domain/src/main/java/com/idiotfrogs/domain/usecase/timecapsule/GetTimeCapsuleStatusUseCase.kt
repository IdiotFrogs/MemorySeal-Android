package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetTimeCapsuleStatusUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository,
) {
    suspend operator fun invoke(capsuleId: Long): Result<TimeCapsuleStatus> = safeCatching {
        timeCapsuleRepository.getTimeCapsuleStatus(capsuleId)
    }
}