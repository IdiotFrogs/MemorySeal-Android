package com.idiotfrogs.data.repository.timecapsule

import com.idiotfrogs.data.datasource.timecapsule.TimeCapsuleDataSource
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateRequest
import com.idiotfrogs.model.timecapsule.TimeCapsuleCreateResponse
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
}