package com.idiotfrogs.data.datasource.user

import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.model.user.UserResponse
import com.idiotfrogs.model.user.UserUpdateRequest
import okhttp3.MultipartBody

interface UserDataSource {
    suspend fun getMyProfile(): ProfileResponse

    suspend fun withdraw()

    suspend fun updateMyProfile(
        userId: Long,
        profileImage: MultipartBody.Part,
        nickname: String,
    ): UserResponse

    suspend fun signUp(
        nickname: String,
        profileImage: MultipartBody.Part
    ): UserResponse
}