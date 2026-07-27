package com.idiotfrogs.domain.usecase.user

import com.idiotfrogs.data.repository.user.UserRepository
import com.idiotfrogs.model.user.UserResponse
import com.idiotfrogs.util.safeCatching
import java.io.File
import javax.inject.Inject

class UpdateMyProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        profileImage: File?,
        nickname: String,
        useDefaultImage: Boolean
    ): Result<UserResponse> = safeCatching {
            userRepository.updateMyProfile(
                profileImage,
                nickname,
                useDefaultImage
            )
        }
}