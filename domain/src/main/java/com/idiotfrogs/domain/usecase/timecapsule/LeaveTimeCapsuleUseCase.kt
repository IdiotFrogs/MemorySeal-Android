package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class LeaveTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(capsuleId: Long) = safeCatching {
        timeCapsuleRepository.leaveTimeCapsule(capsuleId = capsuleId)
    }
}