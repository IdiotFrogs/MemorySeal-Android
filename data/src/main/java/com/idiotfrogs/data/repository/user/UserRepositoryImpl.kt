package com.idiotfrogs.data.repository.user

import com.idiotfrogs.data.datasource.user.UserDataSource
import com.idiotfrogs.model.user.UserResponse
import com.idiotfrogs.model.user.UserUpdateRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {
    override suspend fun getMyProfile(): UserResponse {
        return userDataSource.getMyProfile()
    }

    override suspend fun updateMyProfile(
        userId: Long,
        profileImage: File,
        userUpdateRequest: UserUpdateRequest
    ): UserResponse {
        val imageRequestBody = profileImage.asRequestBody("image/jpeg".toMediaType())
        val imagePart = MultipartBody.Part.createFormData("profileImage", profileImage.name, imageRequestBody)
        return userDataSource.updateMyProfile(
            userId = userId,
            profileImage = imagePart,
            userUpdateRequest = userUpdateRequest
        )
    }
}