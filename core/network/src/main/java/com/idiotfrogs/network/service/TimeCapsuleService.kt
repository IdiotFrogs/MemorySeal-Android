package com.idiotfrogs.network.service

import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleInviteCodeResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
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
    suspend fun deleteTimeCapsule(@Path("capsuleId") capsuleId: Long)

    @GET("time-capsules/{capsuleId}")
    suspend fun getTimeCapsule(@Path("capsuleId") capsuleId: Long): TimeCapsuleResponse

    @GET("time-capsules/{capsuleId}/collaborators")
    suspend fun getTimesCapsuleCollaborators(@Path("capsuleId") capsuleId: Long): List<TimeCapsuleCollaboratorsResponse>

    @POST("time-capsules/{capsuleId}/invite")
    suspend fun getTimeCapsuleInviteCode(@Path("capsuleId") capsuleId: Long): TimeCapsuleInviteCodeResponse
}