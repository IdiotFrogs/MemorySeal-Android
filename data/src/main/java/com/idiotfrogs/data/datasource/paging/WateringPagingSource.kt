package com.idiotfrogs.data.datasource.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.idiotfrogs.model.timecapsule.WateringContentResponse
import com.idiotfrogs.model.timecapsule.WateringMeta
import com.idiotfrogs.network.service.TimeCapsuleService

class WateringPagingSource(
    private val timeCapsuleService: TimeCapsuleService,
    private val capsuleId: Long,
    private val sort: String,
    // 리스트 외 메타 데이터를 전달하기 위한 callbacks
    private val onMetaLoaded: (wateringMeta: WateringMeta) -> Unit,
): PagingSource<Int, WateringContentResponse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WateringContentResponse> {
        return try {
            val page = params.key ?: 0
            val response = timeCapsuleService.getWatering(capsuleId, page, LOAD_SIZE, sort)

            // 갱신되는 경우 메타도 함께 업데이트
            if (params is LoadParams.Refresh) {
                onMetaLoaded(
                    WateringMeta(
                        totalDays = response.totalDays,
                        wateringCount = response.wateringCount,
                        stage = response.stage
                    )
                )
            }

            LoadResult.Page(
                data = response.waterings.content,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.waterings.last) null else page + 1
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