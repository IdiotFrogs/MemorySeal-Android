package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetUnopenedUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend fun invoke() = safeCatching { timeCapsuleRepository.getUnopened() }
}