package com.idiotfrogs.data.repository.user

import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.model.user.UserResponse
import com.idiotfrogs.model.user.UserUpdateRequest
import java.io.File

interface UserRepository {
    suspend fun getMyProfile(): ProfileResponse

    suspend fun withdraw()

    suspend fun updateMyProfile(
        userId: Long,
        profileImage: File?,
        nickname: String
    ): UserResponse

    suspend fun signUp(
        nickname: String,
        profileImage: File,
    ): UserResponse
}