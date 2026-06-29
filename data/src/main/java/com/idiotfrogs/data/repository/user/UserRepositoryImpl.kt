package com.idiotfrogs.data.repository.user

import com.idiotfrogs.data.datasource.user.UserDataSource
import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.model.user.UserResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {
    override suspend fun getMyProfile(): ProfileResponse {
        return userDataSource.getMyProfile()
    }

    override suspend fun withdraw() {
        userDataSource.withdraw()
    }

    override suspend fun updateMyProfile(
        userId: Long,
        profileImage: File?,
        nickname: String,
    ): UserResponse {
        val imageRequestBody = profileImage?.asRequestBody("image/jpeg".toMediaType())
            ?: "".toRequestBody("image/*".toMediaType())
        val imagePart = MultipartBody.Part.createFormData(
            "profileImage",
            profileImage?.name ?: "profileImage", // 기본 이미지 대응
            imageRequestBody
        )

        return userDataSource.updateMyProfile(
            userId = userId,
            profileImage = imagePart,
            nickname = nickname
        )
    }

    override suspend fun signUp(
        nickname: String,
        profileImage: File
    ): UserResponse {
        val imageRequestBody = profileImage.asRequestBody("image/jpeg".toMediaType())
        val imagePart = MultipartBody.Part.createFormData("profileImage", profileImage.name, imageRequestBody)
        return userDataSource.signUp(
            nickname = nickname,
            profileImage = imagePart
        )
    }
}