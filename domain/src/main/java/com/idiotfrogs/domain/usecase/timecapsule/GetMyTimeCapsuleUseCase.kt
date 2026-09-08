package com.idiotfrogs.domain.usecase.timecapsule

import com.idiotfrogs.data.repository.timecapsule.TimeCapsuleRepository
import com.idiotfrogs.model.timecapsule.MyTimeCapsuleContent
import com.idiotfrogs.model.timecapsule.TimeCapsuleRole
import com.idiotfrogs.model.timecapsule.TimeCapsuleStatus
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetMyTimeCapsuleUseCase @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) {
    suspend operator fun invoke(): Result<Map<TimeCapsuleRole, List<MyTimeCapsuleContent>>> =
        safeCatching {
            // FIXME: 임시 코드
            (timeCapsuleRepository.getMyTimeCapsule(
                status = TimeCapsuleStatus.BURIED,
                page = 0,
                size = 50
            ).content + timeCapsuleRepository.getMyTimeCapsule(
                status = TimeCapsuleStatus.BEFOREBURIED,
                page = 0,
                size = 50
            ).content).groupBy { it.role }
        }
}