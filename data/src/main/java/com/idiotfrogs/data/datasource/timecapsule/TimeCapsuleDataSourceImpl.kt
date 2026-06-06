package com.idiotfrogs.data.datasource.timecapsule

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
import com.idiotfrogs.network.service.TimeCapsuleService
import okhttp3.MultipartBody
import javax.inject.Inject

class TimeCapsuleDataSourceImpl @Inject constructor(
    private val timeCapsuleService: TimeCapsuleService
) : TimeCapsuleDataSource {
    override suspend fun createTimeCapsule(
        timeCapsuleCreateDto: TimeCapsuleCreateRequest,
        mainImage: MultipartBody.Part
    ): TimeCapsuleCreateResponse {
        return timeCapsuleService.createTimeCapsule(
            timeCapsuleCreateDto,
            mainImage
        )
    }

    override suspend fun getMyTimeCapsule(): List<MyTimeCapsuleResponse> {
        return timeCapsuleService.getMyTimeCapsule()
    }

    override suspend fun deleteTimeCapsule(capsuleId: Long) {
        return timeCapsuleService.deleteTimeCapsule(capsuleId)
    }

    override suspend fun getTimeCapsule(capsuleId: Long): TimeCapsuleResponse {
        return timeCapsuleService.getTimeCapsule(capsuleId)
    }

    override suspend fun getTimesCapsuleCollaborators(
        capsuleId: Long,
        page: Int,
        size: Int
    ): TimeCapsuleCollaboratorsResponse {
        return timeCapsuleService.getTimesCapsuleCollaborators(capsuleId, page, size)
    }

    override suspend fun getTimeCapsuleInviteCode(capsuleId: Long): TimeCapsuleInviteCodeResponse {
        return timeCapsuleService.getTimeCapsuleInviteCode(capsuleId)
    }

    override suspend fun getRequestCollaborators(capsuleId: Long): List<RequestCollaboratorsResponse> {
        return timeCapsuleService.getRequestCollaborators(capsuleId)
    }

    override suspend fun requestCollaborator(body: PendingCollaboratorsRequest) {
        return timeCapsuleService.requestCollaborator(body)
    }

    override suspend fun processRequest(requestId: Long, body: ProcessCollaboratorRequest) {
        return timeCapsuleService.processRequest(requestId , body)
    }

    override suspend fun buryTimeCapsule(
        capsuleId: Long,
        body: BuryTimeCapsuleRequest
    ): TimeCapsuleResponse {
        return timeCapsuleService.buryTimeCapsule(capsuleId, body)
    }
}