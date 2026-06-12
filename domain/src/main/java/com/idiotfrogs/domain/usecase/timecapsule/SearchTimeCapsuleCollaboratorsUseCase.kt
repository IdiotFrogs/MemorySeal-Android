package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class SearchTimeCapsuleCollaboratorsUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(
        capsuleId: Long,
        nickname: String,
        page: Int,
        size: Int,
    ): Result<TimeCapsuleCollaboratorsResponse> = safeCatching {
        timeCapsuleRepository.searchTimesCapsuleCollaborators(
            capsuleId = capsuleId,
            nickname = nickname,
            page = page,
            size = size
        )
    }
}
