package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetTimeCapsuleCollaboratorsUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(
        capsuleId: Long,
        page: Int,
        size: Int,
    ): Result<TimeCapsuleCollaboratorsResponse> = safeCatching {
        timeCapsuleRepository.getTimesCapsuleCollaborators(capsuleId, page, size)
    }
}