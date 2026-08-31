package com.idiotfrogs.util.paging

data class PaginationState<T>(
    val items: List<T> = emptyList(),
    val currentPage: Int = -1,
    val totalElements: Long = 0L,
    val isLast: Boolean = false,
    val isLoadingMore: Boolean = false,
) {
    val canLoadMore: Boolean
        get() = currentPage >= 0 && !isLast && !isLoadingMore

    fun clear(): PaginationState<T> = PaginationState()

    fun setLoadingMore(isLoadingMore: Boolean): PaginationState<T> =
        copy(isLoadingMore = isLoadingMore)

    fun addPage(
        newItems: List<T>,
        page: Int,
        totalElements: Long,
        isLast: Boolean,
    ): PaginationState<T> = copy(
        items = if (page == 0) newItems else items + newItems,
        currentPage = page,
        totalElements = totalElements,
        isLast = isLast,
        isLoadingMore = false,
    )
}
