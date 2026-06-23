package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.CapsuleContentsData
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetMyTimeCapsuleContentUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(timeCapsuleId: Long): Result<List<CapsuleContentsData>> = safeCatching {
        timeCapsuleRepository.getMyTimeCapsuleContent(timeCapsuleId)
    }
}
