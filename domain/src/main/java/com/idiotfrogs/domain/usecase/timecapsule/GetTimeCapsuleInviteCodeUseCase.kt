package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.TimeCapsuleInviteCodeResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetTimeCapsuleInviteCodeUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(capsuleId: Long): Result<TimeCapsuleInviteCodeResponse> = safeCatching {
        timeCapsuleRepository.getTimeCapsuleInviteCode(capsuleId)
    }
}