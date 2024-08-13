package com.ssafy.yoganavi.data.source.lecture

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.ui.utils.PAGE_SIZE

class LecturePagingSource(
    private val lectureAPI: LectureAPI,
    private val sort: String,
    private val keyword: String? = null,
    private val title: Boolean = true,
    private val content: Boolean = true,
) : PagingSource<Int, LectureData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LectureData> {
        val page = params.key ?: 0
        val result = runCatching {
            if (keyword.isNullOrBlank()) {
                lectureAPI.getRecordedLectures(sort, page, params.loadSize)
            } else {
                lectureAPI.getRecordedLecturesByKeyword(
                    sort, keyword, page, params.loadSize, title, content
                )
            }
        }

        return result.fold(
            onSuccess = { response ->
                val repos = response.body()?.data ?: emptyList()
                val nextKey = if (repos.isEmpty()) null else page + (params.loadSize / PAGE_SIZE)
                val prevKey = if (page == 0) null else page - 1
                LoadResult.Page(repos, prevKey, nextKey)
            },
            onFailure = { exception ->
                LoadResult.Error(exception)
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, LectureData>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }
}