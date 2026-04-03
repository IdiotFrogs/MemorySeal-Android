package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleResponse
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetMyTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(): Result<Map<TimeCapsuleRole, List<MyTimeCapsuleResponse>>> =
        safeCatching {
            timeCapsuleRepository.getMyTimeCapsule()
                .filter { it.timeCapsuleStatus != TimeCapsuleStatus.OPENED }
                .groupBy { it.role }
        }
}