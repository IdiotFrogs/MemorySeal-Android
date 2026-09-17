package com.idiotfrogs.data.datasource.home

import com.idiotfrogs.model.home.SeasonBannerResponse

interface HomeDataSource {
    suspend fun getSeasonBanner(): SeasonBannerResponse?
}