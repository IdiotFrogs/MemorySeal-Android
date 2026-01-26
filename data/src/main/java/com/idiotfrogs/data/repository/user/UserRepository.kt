package com.idiotfrogs.data.repository.user

import com.idiotfrogs.model.user.UserResponse
import com.idiotfrogs.model.user.UserUpdateRequest
import java.io.File

interface UserRepository {
    suspend fun getMyProfile(): UserResponse

    suspend fun updateMyProfile(
        userId: Long,
        profileImage: File,
        userUpdateRequest: UserUpdateRequest
    ): UserResponse
}