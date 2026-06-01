package com.idiotfrogs.data.repository.timecapsule

import com.idiotfrogs.model.timecapsule.BuryTimeCapsuleRequest
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.PendingCollaboratorsRequest
import com.idiotfrogs.model.timecapsule.ProcessCollaboratorRequest
import com.idiotfrogs.model.timecapsule.RequestCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleInviteCodeResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
import java.io.File

interface TimeCapsuleRepository {
    suspend fun createTimeCapsule(
        timeCapsuleCreateDto: TimeCapsuleCreateRequest,
        mainImage: File
    ): TimeCapsuleCreateResponse

    suspend fun getMyTimeCapsule(): List<MyTimeCapsuleResponse>

    suspend fun deleteTimeCapsule(capsuleId: Long)

    suspend fun getTimeCapsule(capsuleId: Long): TimeCapsuleResponse

    suspend fun getTimesCapsuleCollaborators(capsuleId: Long): List<TimeCapsuleCollaboratorsResponse>

    suspend fun getTimeCapsuleInviteCode(capsuleId: Long): TimeCapsuleInviteCodeResponse

    suspend fun getRequestCollaborators(capsuleId: Long): List<RequestCollaboratorsResponse>

    suspend fun requestCollaborator(body: PendingCollaboratorsRequest)

    suspend fun processRequest(requestId: Long, body: ProcessCollaboratorRequest)

    suspend fun buryTimeCapsule(
        capsuleId: Long,
        body: BuryTimeCapsuleRequest
    ): TimeCapsuleResponse
}