package com.idiotfrogs.data.datasource.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.idiotfrogs.model.timecapsule.WateringContentResponse
import com.idiotfrogs.network.service.TimeCapsuleService

class WateringPagingSource(
    private val timeCapsuleService: TimeCapsuleService,
    private val capsuleId: Long
): PagingSource<Int, WateringContentResponse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WateringContentResponse> {
        return try {
            val page = params.key ?: 1
            val response = timeCapsuleService.getWatering(capsuleId, page, LOAD_SIZE)

            LoadResult.Page(
                data = response.watering.content,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.watering.last) null else page + 1
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, WateringContentResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        const val LOAD_SIZE = 50 // iOS와 통일
    }
}