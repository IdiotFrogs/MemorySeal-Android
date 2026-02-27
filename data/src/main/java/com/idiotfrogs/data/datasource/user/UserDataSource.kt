package com.idiotfrogs.data.datasource.user

import com.idiotfrogs.model.user.UserResponse
import com.idiotfrogs.model.user.UserUpdateRequest
import okhttp3.MultipartBody

interface UserDataSource {
    suspend fun getMyProfile(): UserResponse

    suspend fun updateMyProfile(
        userId: Long,
        profileImage: MultipartBody.Part,
        userUpdateRequest: UserUpdateRequest
    ): UserResponse

    suspend fun signUp(
        nickname: String,
        profileImage: MultipartBody.Part
    ): UserResponse
}