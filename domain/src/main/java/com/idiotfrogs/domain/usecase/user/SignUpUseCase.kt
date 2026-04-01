package com.idiotfrogs.domain.usecase.user

import com.idiotfrogs.data.repository.user.UserRepository
import com.idiotfrogs.model.user.UserResponse
import com.idiotfrogs.util.safeCatching
import java.io.File
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(nickname: String, profileImage: File): Result<UserResponse> =
        safeCatching { userRepository.signUp(nickname, profileImage) }
}