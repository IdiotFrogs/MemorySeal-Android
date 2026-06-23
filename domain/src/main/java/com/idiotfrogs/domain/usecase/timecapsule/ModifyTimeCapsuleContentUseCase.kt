package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.CapsuleContentsData
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class ModifyTimeCapsuleContentUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(
        contentId: Long,
        content: String,
    ): Result<CapsuleContentsData> = safeCatching {
        timeCapsuleRepository.modifyTimeCapsuleContent(
            contentId = contentId,
            content = content
        )
    }
}
