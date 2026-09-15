package com.idiotfrogs.data.repository.home

import com.idiotfrogs.data.datasource.home.HomeDataSource
import com.idiotfrogs.model.home.SeasonBannerResponse
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val dataSource: HomeDataSource
) : HomeRepository {
    override suspend fun getSeasonBanner(): SeasonBannerResponse? {
        return dataSource.getSeasonBanner()
    }
}