package com.idiotfrogs.domain.usecase.local

import com.idiotfrogs.data.repository.local.LocalRepository
import javax.inject.Inject

class SetTutorialCompletedUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke() = localRepository.setTutorialCompleted()
}
