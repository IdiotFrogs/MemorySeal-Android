package com.idiotfrogs.data.repository.timecapsule

import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
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
}