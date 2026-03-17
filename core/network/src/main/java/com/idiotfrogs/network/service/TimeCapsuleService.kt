package com.idiotfrogs.network.service

import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TimeCapsuleService {

    @POST("time-capsules/create")
    @Multipart
    suspend fun createTimeCapsule(
        @Part("timeCapsuleCreateDto") timeCapsuleCreateDto: TimeCapsuleCreateRequest,
        @Part mainImage: MultipartBody.Part
    ): TimeCapsuleCreateResponse

}