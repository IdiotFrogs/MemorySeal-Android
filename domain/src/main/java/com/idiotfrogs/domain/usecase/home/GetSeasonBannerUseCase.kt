package com.idiotfrogs.domain.usecase.home

import com.idiotfrogs.data.repository.home.HomeRepository
import com.idiotfrogs.model.home.SeasonBannerResponse
import com.idiotfrogs.util.safeCatching
import javax.inject.Inject

class GetSeasonBannerUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend fun invoke(): Result<SeasonBannerResponse?> = safeCatching {
        repository.getSeasonBanner()
    }
}