package com.idiotfrogs.network.service

import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface TimeCapsuleService {

    @POST("time-capsules/create")
    @Multipart
    suspend fun createTimeCapsule(
        @Part("timeCapsuleCreateDto") timeCapsuleCreateDto: TimeCapsuleCreateRequest,
        @Part mainImage: MultipartBody.Part
    ): TimeCapsuleCreateResponse

    @GET("time-capsules/my")
    suspend fun getMyTimeCapsule(): List<MyTimeCapsuleResponse>

    @DELETE("time-capsules/{capsuleId}")
    suspend fun deleteCapsule(@Path("capsuleId") capsuleId: Long)
}