package com.idiotfrogs.network.service

import com.idiotfrogs.model.timecapsule.BuryTimeCapsuleRequest
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.ProcessCollaboratorRequest
import com.idiotfrogs.model.timecapsule.PendingCollaboratorsRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleInviteCodeResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
    suspend fun getTimesCapsuleCollaborators(
        @Path("capsuleId") capsuleId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): TimeCapsuleCollaboratorsResponse

    @POST("time-capsules/{capsuleId}/invite")
    suspend fun getTimeCapsuleInviteCode(@Path("capsuleId") capsuleId: Long): TimeCapsuleInviteCodeResponse

    @POST("time-capsules/join-request")
    suspend fun requestCollaborator(@Body body: PendingCollaboratorsRequest)

    @POST("time-capsules/request/{requestId}/process")
    suspend fun processRequest(
        @Path("requestId") requestId: Long,
        @Body body: ProcessCollaboratorRequest
    )

    @PUT("time-capsules/{capsuleId}/bury")
    suspend fun buryTimeCapsule(
        @Path("capsuleId") capsuleId: Long,
        @Body body: BuryTimeCapsuleRequest
    ): TimeCapsuleResponse

    @PUT("time-capsules/{capsuleId}/delegation/{targetUserId}")
    suspend fun delegationTimeCapsuleHost(
        @Path("capsuleId") capsuleId: Long,
        @Path("targetUserId") targetUserId: Long,
    )

    @DELETE("time-capsules/{capsuleId}/contributors/{targetUserId}")
    suspend fun deleteTimesCapsuleContributors(
        @Path("capsuleId") capsuleId: Long,
        @Path("targetUserId") targetUserId: Long,
    )

    @GET("time-capsules/{capsuleId}/collaborators/search")
    suspend fun searchTimesCapsuleCollaborators(
        @Path("capsuleId") capsuleId: Long,
        @Query("nickname") nickname: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): TimeCapsuleCollaboratorsResponse
}
