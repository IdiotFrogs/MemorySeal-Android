package com.idiotfrogs.domain.usecase.user

import com.idiotfrogs.data.repository.user.UserRepository
import com.idiotfrogs.model.user.UserResponse
import com.idiotfrogs.model.user.UserUpdateRequest
import java.io.File
import javax.inject.Inject

class UpdateMyProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long, profileImage: File, userUpdateRequest: UserUpdateRequest): UserResponse {
        return userRepository.updateMyProfile(userId, profileImage, userUpdateRequest)
    }
}