package com.idiotfrogs.domain.usecase.auth

import com.idiotfrogs.data.repository.auth.AuthRepository
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class PutFcmTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(fcmToken: String): Result<Unit> =
        safeCatching { authRepository.putFcmToken(fcmToken) }
}
