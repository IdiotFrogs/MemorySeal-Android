package com.idiotfrogs.network.service

import com.idiotfrogs.model.user.ProfileResponse
import com.idiotfrogs.model.user.UserResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface UserService {
    @GET("users/me")
    suspend fun getMyProfile(): ProfileResponse

    @DELETE("users/me")
    suspend fun withdraw()

    @PUT("users/me")
    @Multipart
    suspend fun updateMyProfile(
        @Part profileImage: MultipartBody.Part,
        @Query("nickname") nickname: String,
        @Query("resetProfileImage") useDefaultImage: Boolean,
    ): UserResponse

    @PATCH("users/sign-up")
    @Multipart
    suspend fun signUp(
        @Query("nickname") nickname: String,
        @Part profileImage: MultipartBody.Part
    ): UserResponse
}