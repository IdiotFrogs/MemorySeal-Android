package com.idiotfrogs.data.datasource.timecapsule

import com.idiotfrogs.model.timecapsule.BuryTimeCapsuleRequest
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.PendingCollaboratorsRequest
import com.idiotfrogs.model.timecapsule.ProcessCollaboratorRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleInviteCodeResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
import okhttp3.MultipartBody

interface TimeCapsuleDataSource {
    suspend fun createTimeCapsule(
        timeCapsuleCreateDto: TimeCapsuleCreateRequest,
        mainImage: MultipartBody.Part
    ): TimeCapsuleCreateResponse

    suspend fun getMyTimeCapsule(): List<MyTimeCapsuleResponse>

    suspend fun deleteTimeCapsule(capsuleId: Long)

    suspend fun getTimeCapsule(capsuleId: Long): TimeCapsuleResponse

    suspend fun getTimesCapsuleCollaborators(
        capsuleId: Long,
        page: Int,
        size: Int,
    ): TimeCapsuleCollaboratorsResponse

    suspend fun getTimeCapsuleInviteCode(capsuleId: Long): TimeCapsuleInviteCodeResponse

    suspend fun requestCollaborator(body: PendingCollaboratorsRequest)

    suspend fun processRequest(requestId: Long, body: ProcessCollaboratorRequest)

    suspend fun buryTimeCapsule(
        capsuleId: Long,
        body: BuryTimeCapsuleRequest
    ): TimeCapsuleResponse

    suspend fun delegationTimeCapsuleHost(
        capsuleId: Long,
        targetUserId: Long,
    )

    suspend fun deleteTimesCapsuleContributors(
        capsuleId: Long,
        targetUserId: Long,
    )

    suspend fun searchTimesCapsuleCollaborators(
        capsuleId: Long,
        nickname: String,
        page: Int,
        size: Int,
    ): TimeCapsuleCollaboratorsResponse
}
