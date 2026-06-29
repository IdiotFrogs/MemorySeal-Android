package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class DeleteTimeCapsuleContentUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(
        contentIds: List<Long>,
        fileIds: List<Long>,
    ): Result<Unit> = safeCatching {
        timeCapsuleRepository.deleteTimeCapsuleContent(
            contentIds = contentIds,
            fileIds = fileIds
        )
    }
}
