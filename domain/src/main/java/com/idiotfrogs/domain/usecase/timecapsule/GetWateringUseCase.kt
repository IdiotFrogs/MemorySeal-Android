package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.WateringResponse
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetWateringUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend fun invoke(
        capsuleId: Long,
        page: Int,
        size: Int,
        sort: String,
    ): Result<WateringResponse> = safeCatching {
        timeCapsuleRepository.getWatering(
            capsuleId = capsuleId,
            page = page,
            size = size,
            sort = sort
        )
    }
}