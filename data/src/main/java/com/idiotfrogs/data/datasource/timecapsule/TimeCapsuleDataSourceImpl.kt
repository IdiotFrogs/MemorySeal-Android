package com.idiotfrogs.data.datasource.timecapsule

import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
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
}