package com.idiotfrogs.data.datasource.home

import com.idiotfrogs.model.home.SeasonBannerResponse
import com.idiotfrogs.network.service.HomeService
import javax.inject.Inject

class HomeDataSourceImpl @Inject constructor(
    private val service: HomeService
) : HomeDataSource {
    override suspend fun getSeasonBanner(): SeasonBannerResponse? {
        return service.getSeasonBanner()
    }
}