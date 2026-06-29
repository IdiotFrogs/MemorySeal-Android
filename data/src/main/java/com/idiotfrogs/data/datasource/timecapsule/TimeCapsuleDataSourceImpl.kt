package com.idiotfrogs.data.datasource.timecapsule

import com.idiotfrogs.model.timecapsule.BuryTimeCapsuleRequest
import com.idiotfrogs.model.timecapsule.CapsuleContentsData
import com.idiotfrogs.model.timecapsule.MyCapsuleContentsData
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.PendingCollaboratorsRequest
import com.idiotfrogs.model.timecapsule.ProcessCollaboratorRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleContentResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleInviteCodeResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
import com.idiotfrogs.network.service.TimeCapsuleService
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    override suspend fun delegationTimeCapsuleHost(
        capsuleId: Long,
        targetUserId: Long
    ) {
        return timeCapsuleService.delegationTimeCapsuleHost(capsuleId, targetUserId)
    }

    override suspend fun deleteTimesCapsuleContributors(
        capsuleId: Long,
        targetUserId: Long
    ) {
        return timeCapsuleService.deleteTimesCapsuleContributors(capsuleId, targetUserId)
    }

    override suspend fun searchTimesCapsuleCollaborators(
        capsuleId: Long,
        nickname: String,
        page: Int,
        size: Int
    ): TimeCapsuleCollaboratorsResponse {
        return timeCapsuleService.searchTimesCapsuleCollaborators(
            capsuleId = capsuleId,
            nickname = nickname,
            page = page,
            size = size
        )
    }

    override suspend fun leaveTimeCapsule(capsuleId: Long) {
        return timeCapsuleService.leaveTimeCapsule(capsuleId)
    }

    override suspend fun getTimeCapsuleContent(
        timeCapsuleId: Long,
        page: Int,
        size: Int,
    ): TimeCapsuleContentResponse {
        return timeCapsuleService.getTimeCapsuleContent(
            timeCapsuleId = timeCapsuleId,
            page = page,
            size = size
        )
    }

    override suspend fun getMyTimeCapsuleContent(timeCapsuleId: Long): List<MyCapsuleContentsData> {
        return timeCapsuleService.getMyTimeCapsuleContent(timeCapsuleId)
    }

    override suspend fun createTimeCapsuleContent(
        timeCapsuleId: Long,
        content: RequestBody,
        files: List<MultipartBody.Part>
    ): CapsuleContentsData {
        return timeCapsuleService.createTimeCapsuleContent(
            timeCapsuleId = timeCapsuleId,
            content = content,
            files = files
        )
    }

    override suspend fun modifyTimeCapsuleContent(
        contentId: Long,
        content: String
    ): CapsuleContentsData {
        return timeCapsuleService.modifyTimeCapsuleContent(
            contentId = contentId,
            content = content
        )
    }

    override suspend fun deleteTimeCapsuleContent(
        contentIds: List<Long>,
        fileIds: List<Long>,
    ) {
        return timeCapsuleService.deleteTimeCapsuleContent(
            contentIds = contentIds,
            fileIds = fileIds
        )
    }
}
