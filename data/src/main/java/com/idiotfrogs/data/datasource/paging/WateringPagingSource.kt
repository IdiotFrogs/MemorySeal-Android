package com.idiotfrogs.data.datasource.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.idiotfrogs.model.timecapsule.WateringContentResponse
import com.idiotfrogs.model.timecapsule.WateringMeta
import com.idiotfrogs.network.service.TimeCapsuleService
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class WateringPagingSource(
    private val timeCapsuleService: TimeCapsuleService,
    private val capsuleId: Long,
    private val sort: String,
    // 리스트 외 메타 데이터를 전달하기 위한 callbacks
    private val onMetaLoaded: (wateringMeta: WateringMeta) -> Unit,
) : PagingSource<Int, WateringContentResponse>() {
    // 다음 페이지가 시작할 날짜
    private var cursor: LocalDate? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WateringContentResponse> {
        return try {
            val page = params.key ?: 0
            val response = timeCapsuleService.getWatering(capsuleId, page, LOAD_SIZE, sort)

            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val buriedDay = today.minus(response.totalDays - 1, DateTimeUnit.DAY)

            val content = response.waterings.content
            val byDate = content.associateBy { it.wateredDate }

            val isDesc = sort.equals("desc", ignoreCase = true)

            val start = cursor ?: if (isDesc) today else buriedDay
            val end = when {
                response.waterings.last -> if (isDesc) buriedDay else today
                content.isEmpty() -> start
                else -> content.last().wateredDate
            }
            val dates = if (isDesc) {
                generateSequence(start) {
                    it.minus(1, DateTimeUnit.DAY).takeIf { day -> day >= end }
                }
            } else {
                generateSequence(start) {
                    it.plus(1, DateTimeUnit.DAY).takeIf { day -> day <= end }
                }
            }
            val filled = dates.map {
                byDate[it] ?: WateringContentResponse(it, false, null, null)
            }.toList()
            cursor = if (isDesc) end.minus(1, DateTimeUnit.DAY) else end.plus(1, DateTimeUnit.DAY)
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

            Log.d("TAG", filled.toString())

            LoadResult.Page(
                data = filled,
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