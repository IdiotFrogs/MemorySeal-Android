package com.idiotfrogs.data.datasource.user

import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.model.user.UserResponse
import com.idiotfrogs.model.user.UserUpdateRequest
import com.idiotfrogs.network.service.UserService
import okhttp3.MultipartBody
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val userService: UserService
) : UserDataSource {
    override suspend fun getMyProfile(): ProfileResponse {
        return userService.getMyProfile()
    }

    override suspend fun withdraw() {
        userService.withdraw()
    }

    override suspend fun updateMyProfile(
        userId: Long,
        profileImage: MultipartBody.Part,
        userUpdateRequest: UserUpdateRequest
    ): UserResponse {
        return userService.updateMyProfile(
            userId = userId,
            profileImage = profileImage,
            userUpdateRequest = userUpdateRequest
        )
    }

    override suspend fun signUp(
        nickname: String,
        profileImage: MultipartBody.Part
    ): UserResponse {
        return userService.signUp(
            nickname = nickname,
            profileImage = profileImage
        )
    }
}