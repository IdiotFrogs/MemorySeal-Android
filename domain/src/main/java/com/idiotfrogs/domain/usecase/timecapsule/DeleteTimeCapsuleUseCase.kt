package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class DeleteTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(capsuleId: Long): Result<Unit> = safeCatching {
        timeCapsuleRepository.deleteTimeCapsule(capsuleId)
    }
}