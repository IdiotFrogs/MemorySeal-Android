package com.idiotfrogs.domain.usecase.user

import com.idiotfrogs.data.repository.user.UserRepository
import com.idiotfrogs.model.user.UserResponse
import com.idiotfrogs.model.user.UserUpdateRequest
import com.idiotfrogs.util.safeCatching
import java.io.File
import javax.inject.Inject

class UpdateMyProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: Long,
        profileImage: File,
        userUpdateRequest: UserUpdateRequest
    ): Result<UserResponse> = safeCatching {
            userRepository.updateMyProfile(
                userId,
                profileImage,
                userUpdateRequest
            )
        }
}