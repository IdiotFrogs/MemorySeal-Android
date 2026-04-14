package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.RequestCollaboratorsResponse
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetRequestCollaboratorsUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(capsuleId: Long): Result<List<RequestCollaboratorsResponse>> = safeCatching {
        timeCapsuleRepository.getRequestCollaborators(capsuleId)
    }
}