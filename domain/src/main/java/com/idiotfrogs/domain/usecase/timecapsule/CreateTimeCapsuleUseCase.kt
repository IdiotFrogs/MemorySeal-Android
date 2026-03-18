package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import java.io.File
import javax.inject.Inject

class CreateTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(
        timeCapsuleCreateDto: TimeCapsuleCreateRequest,
        mainImage: File
    ): TimeCapsuleCreateResponse {
        return timeCapsuleRepository.createTimeCapsule(
            timeCapsuleCreateDto,
            mainImage
        )
    }
}