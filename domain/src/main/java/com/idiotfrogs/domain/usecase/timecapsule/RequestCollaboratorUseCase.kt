package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.PendingCollaboratorsRequest
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class RequestCollaboratorUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(body: PendingCollaboratorsRequest): Result<Unit> = safeCatching {
        timeCapsuleRepository.requestCollaborator(body)
    }
}