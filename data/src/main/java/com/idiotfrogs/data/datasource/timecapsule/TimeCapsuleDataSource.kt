package com.idiotfrogs.data.datasource.timecapsule

import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
import okhttp3.MultipartBody

interface TimeCapsuleDataSource {
    suspend fun createTimeCapsule(
        timeCapsuleCreateDto: TimeCapsuleCreateRequest,
        mainImage: MultipartBody.Part
    ): TimeCapsuleCreateResponse

    suspend fun getMyTimeCapsule(): List<MyTimeCapsuleResponse>
}