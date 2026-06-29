package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.TimeCapsuleContentResponse
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetTimeCapsuleContentUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(
        timeCapsuleId: Long,
        page: Int,
        size: Int,
    ): Result<TimeCapsuleContentResponse> = safeCatching {
        timeCapsuleRepository.getTimeCapsuleContent(
            timeCapsuleId = timeCapsuleId,
            page = page,
            size = size
        )
    }
}
