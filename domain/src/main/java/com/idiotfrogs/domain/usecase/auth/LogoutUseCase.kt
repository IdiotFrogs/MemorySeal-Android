package com.idiotfrogs.domain.usecase.auth

import com.idiotfrogs.data.repository.auth.AuthRepository
import com.idiotfrogs.data.repository.local.LocalRepository
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val localRepository: LocalRepository
) {
    suspend fun invoke() = safeCatching {
        authRepository.logout()
        localRepository.clearTokens()
    }
}