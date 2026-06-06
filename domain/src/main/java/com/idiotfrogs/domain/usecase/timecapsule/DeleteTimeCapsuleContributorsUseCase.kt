package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class DeleteTimeCapsuleContributorsUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(capsuleId: Long, targetUserId: Long): Result<Unit> = safeCatching {
        timeCapsuleRepository.deleteTimesCapsuleContributors(capsuleId, targetUserId)
    }
}
