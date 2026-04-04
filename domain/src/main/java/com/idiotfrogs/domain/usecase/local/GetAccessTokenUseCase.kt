package com.idiotfrogs.domain.usecase.local

import com.idiotfrogs.data.repository.local.LocalRepository
import javax.inject.Inject

class GetAccessTokenUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    val accessToken = localRepository.accessToken
}