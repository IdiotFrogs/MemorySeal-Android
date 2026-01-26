package com.idiotfrogs.data.datasource.user

import com.idiotfrogs.model.user.UserResponse
import com.idiotfrogs.model.user.UserUpdateRequest
import com.idiotfrogs.network.service.UserService
import okhttp3.MultipartBody
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val userService: UserService
) : UserDataSource {
    override suspend fun getMyProfile(): UserResponse {
        return userService.getMyProfile()
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
}