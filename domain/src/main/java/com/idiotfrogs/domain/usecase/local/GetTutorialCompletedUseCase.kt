package com.idiotfrogs.domain.usecase.local

import com.idiotfrogs.data.repository.local.LocalRepository
import javax.inject.Inject

class GetTutorialCompletedUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    val tutorialCompleted = localRepository.tutorialCompleted
}
