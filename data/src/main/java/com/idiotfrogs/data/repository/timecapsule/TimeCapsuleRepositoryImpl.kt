package com.idiotfrogs.data.repository.timecapsule

import com.idiotfrogs.data.datasource.timecapsule.TimeCapsuleDataSource
import com.idiotfrogs.model.timecapsule.BuryTimeCapsuleRequest
import com.idiotfrogs.model.timecapsule.CapsuleContentsData
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.MyCapsuleContentsData
import com.idiotfrogs.model.timecapsule.PendingCollaboratorsRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCollaboratorsResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleContentResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleInviteCodeResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleResponse
import com.idiotfrogs.util.exception.AlreadyContributorException
import com.idiotfrogs.model.timecapsule.WateringResponse
import com.idiotfrogs.network.service.TimeCapsuleService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class TimeCapsuleRepositoryImpl @Inject constructor(
    private val timeCapsuleService: TimeCapsuleService,
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

    override suspend fun joinTimeCapsule(capsuleId: Long): TimeCapsuleResponse {
        return try {
            timeCapsuleDataSource.joinTimeCapsule(capsuleId)
        } catch (exception: HttpException) {
            if (exception.code() == ALREADY_CONTRIBUTOR_STATUS_CODE) {
                throw AlreadyContributorException()
            }
            throw exception
        }
    }

    override suspend fun requestCollaborator(body: PendingCollaboratorsRequest) {
        return timeCapsuleDataSource.requestCollaborator(body)
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

    override suspend fun getTimeCapsuleContent(
        timeCapsuleId: Long,
        page: Int,
        size: Int,
    ): TimeCapsuleContentResponse {
        return timeCapsuleDataSource.getTimeCapsuleContent(
            timeCapsuleId = timeCapsuleId,
            page = page,
            size = size
        )
    }

    override suspend fun getMyTimeCapsuleContent(timeCapsuleId: Long): List<MyCapsuleContentsData> {
        return timeCapsuleDataSource.getMyTimeCapsuleContent(timeCapsuleId)
    }

    override suspend fun createTimeCapsuleContent(
        timeCapsuleId: Long,
        content: String,
        files: List<File>
    ): CapsuleContentsData {
        val contentBody = content.toRequestBody("text/plain".toMediaType())
        val fileParts = files.map {
            val requestBody = it.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("files", it.name, requestBody)
        }

        return timeCapsuleDataSource.createTimeCapsuleContent(
            timeCapsuleId = timeCapsuleId,
            content = contentBody,
            files = fileParts
        )
    }

    override suspend fun modifyTimeCapsuleContent(
        contentId: Long,
        content: String
    ): CapsuleContentsData {
        return timeCapsuleDataSource.modifyTimeCapsuleContent(
            contentId = contentId,
            content = content
        )
    }

    override suspend fun deleteTimeCapsuleContent(
        contentIds: List<Long>,
        fileIds: List<Long>,
    ) {
        return timeCapsuleDataSource.deleteTimeCapsuleContent(
            contentIds = contentIds,
            fileIds = fileIds
        )
    }

    override suspend fun getWatering(
        capsuleId: Long,
        page: Int,
        size: Int,
        sort: String
    ): WateringResponse {
        return timeCapsuleDataSource.getWatering(
            capsuleId = capsuleId,
            page = page,
            size = size,
            sort = sort
        )
    }

    override suspend fun watering(capsuleId: Long) {
        return timeCapsuleDataSource.watering(capsuleId)
    }


    companion object {
        private const val ALREADY_CONTRIBUTOR_STATUS_CODE = 409
    }
}
