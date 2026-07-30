package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.local.LocalRepository
import javax.inject.Inject

class GetViewedTimeCapsuleUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    val capsuleIds = localRepository.capsuleIds
}