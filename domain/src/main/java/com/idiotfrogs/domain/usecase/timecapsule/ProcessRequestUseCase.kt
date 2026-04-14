package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.ProcessCollaboratorRequest
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class ProcessRequestUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(requestId: Long, body: ProcessCollaboratorRequest): Result<Unit> = safeCatching {
        timeCapsuleRepository.processRequest(requestId, body)
    }
}