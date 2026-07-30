package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.local.LocalRepository
import javax.inject.Inject

class AddViewedTimeCapsuleUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend fun invoke(capsuleId: Long) {
        localRepository.addCapsuleId(capsuleId)
    }
}