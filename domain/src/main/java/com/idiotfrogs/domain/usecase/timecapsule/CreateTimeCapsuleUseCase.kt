package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import com.idiotfrogs.util.safeCatching
import java.io.File
import javax.inject.Inject

class CreateTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(
        timeCapsuleCreateDto: TimeCapsuleCreateRequest,
        mainImage: File
    ): Result<TimeCapsuleCreateResponse> =
        safeCatching {
            timeCapsuleRepository.createTimeCapsule(
                timeCapsuleCreateDto,
                mainImage
            )
        }
}