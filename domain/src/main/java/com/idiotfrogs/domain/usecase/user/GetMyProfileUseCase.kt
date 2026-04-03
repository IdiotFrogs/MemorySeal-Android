package com.idiotfrogs.domain.usecase.user

import com.idiotfrogs.data.repository.user.UserRepository
import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetMyProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<ProfileResponse> =
        safeCatching { userRepository.getMyProfile() }
}