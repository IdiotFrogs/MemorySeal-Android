package com.idiotfrogs.domain.usecase.timecapsule

import androidx.paging.PagingData
import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.WateringContentResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWateringUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    fun invoke(capsuleId: Long): Flow<PagingData<WateringContentResponse>> {
        // 별도 safeCatching 사용 안함, collect 시점에 처리
        return timeCapsuleRepository.getWatering(capsuleId)
    }
}