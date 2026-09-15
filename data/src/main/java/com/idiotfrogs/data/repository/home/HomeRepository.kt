package com.idiotfrogs.data.repository.home

import com.idiotfrogs.model.home.SeasonBannerResponse

interface HomeRepository {
    suspend fun getSeasonBanner(): SeasonBannerResponse?
}