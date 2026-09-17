package com.idiotfrogs.network.service

import com.idiotfrogs.model.home.SeasonBannerResponse
import retrofit2.http.GET

interface HomeService {
    @GET("banners/seasonal")
    suspend fun getSeasonBanner(): SeasonBannerResponse?
}