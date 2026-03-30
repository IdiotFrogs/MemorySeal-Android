package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import javax.inject.Inject

class GetMyTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(): Map<TimeCapsuleRole, List<MyTimeCapsuleResponse>> {
        val result = timeCapsuleRepository.getMyTimeCapsule()
        return result
            .filter { it.timeCapsuleStatus != TimeCapsuleStatus.OPENED }
            .groupBy { it.role }
    }
}