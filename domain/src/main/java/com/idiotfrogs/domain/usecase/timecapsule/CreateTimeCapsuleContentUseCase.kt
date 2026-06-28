package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.CapsuleContentsData
import com.idiotfrogs.util.safeCatching
import java.io.File
import javax.inject.Inject

class CreateTimeCapsuleContentUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(
        timeCapsuleId: Long,
        content: String,
        files: List<File>,
    ): Result<CapsuleContentsData> = safeCatching {
        timeCapsuleRepository.createTimeCapsuleContent(
            timeCapsuleId = timeCapsuleId,
            content = content,
            files = files
        )
    }
}
