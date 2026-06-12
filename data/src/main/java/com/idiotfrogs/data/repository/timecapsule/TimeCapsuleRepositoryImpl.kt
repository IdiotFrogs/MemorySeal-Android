package com.idiotfrogs.data.repository.timecapsule

import com.idiotfrogs.data.datasource.timecapsule.TimeCapsuleDataSource
import com.idiotfrogs.model.timecapsule.BuryTimeCapsuleRequest
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.PendingCollaboratorsRequest
import com.idiotfrogs.model.timecapsule.ProcessCollaboratorRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleInviteCodeResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class TimeCapsuleRepositoryImpl @Inject constructor(
    private val timeCapsuleDataSource: TimeCapsuleDataSource
) : TimeCapsuleRepository {
    override suspend fun createTimeCapsule(
        timeCapsuleCreateDto: TimeCapsuleCreateRequest,
        mainImage: File
    ): TimeCapsuleCreateResponse {
        val imageRequestBody = mainImage.asRequestBody("image/jpeg".toMediaType())
        val imagePart = MultipartBody.Part.createFormData("mainImage", mainImage.name, imageRequestBody)
        return timeCapsuleDataSource.createTimeCapsule(
            timeCapsuleCreateDto,
            imagePart
        )
    }

    override suspend fun getMyTimeCapsule(): List<MyTimeCapsuleResponse> {
        return timeCapsuleDataSource.getMyTimeCapsule()
    }

    override suspend fun deleteTimeCapsule(capsuleId: Long) {
        return timeCapsuleDataSource.deleteTimeCapsule(capsuleId)
    }

    override suspend fun getTimeCapsule(capsuleId: Long): TimeCapsuleResponse {
        return timeCapsuleDataSource.getTimeCapsule(capsuleId)
    }

    override suspend fun getTimesCapsuleCollaborators(
        capsuleId: Long,
        page: Int,
        size: Int,
    ): TimeCapsuleCollaboratorsResponse {
        return timeCapsuleDataSource.getTimesCapsuleCollaborators(capsuleId, page, size)
    }

    override suspend fun getTimeCapsuleInviteCode(capsuleId: Long): TimeCapsuleInviteCodeResponse {
        return timeCapsuleDataSource.getTimeCapsuleInviteCode(capsuleId)
    }

    override suspend fun requestCollaborator(body: PendingCollaboratorsRequest) {
        return timeCapsuleDataSource.requestCollaborator(body)
    }

    override suspend fun processRequest(requestId: Long, body: ProcessCollaboratorRequest) {
        return timeCapsuleDataSource.processRequest(requestId, body)
    }

    override suspend fun buryTimeCapsule(
        capsuleId: Long,
        body: BuryTimeCapsuleRequest
    ): TimeCapsuleResponse {
        return timeCapsuleDataSource.buryTimeCapsule(capsuleId, body)
    }

    override suspend fun delegationTimeCapsuleHost(
        capsuleId: Long,
        targetUserId: Long
    ) {
        return timeCapsuleDataSource.delegationTimeCapsuleHost(capsuleId, targetUserId)
    }

    override suspend fun deleteTimesCapsuleContributors(
        capsuleId: Long,
        targetUserId: Long
    ) {
        return timeCapsuleDataSource.deleteTimesCapsuleContributors(capsuleId, targetUserId)
    }

    override suspend fun searchTimesCapsuleCollaborators(
        capsuleId: Long,
        nickname: String,
        page: Int,
        size: Int
    ): TimeCapsuleCollaboratorsResponse {
        return timeCapsuleDataSource.searchTimesCapsuleCollaborators(
            capsuleId = capsuleId,
            nickname = nickname,
            page = page,
            size = size
        )
    }

    override suspend fun leaveTimeCapsule(capsuleId: Long) {
        return timeCapsuleDataSource.leaveTimeCapsule(capsuleId)
    }
}
