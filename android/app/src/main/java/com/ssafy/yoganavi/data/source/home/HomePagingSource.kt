package com.ssafy.yoganavi.data.source.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ssafy.yoganavi.data.source.dto.home.HomeData

class HomePagingSource(
    private val homeAPI: HomeAPI
) : PagingSource<Int, HomeData>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomeData> {
        val page = params.key ?: 0
        return try {
            val response = homeAPI.getHomeList(page, params.loadSize)
            val repos = response.body()?.data ?: emptyList()

            val nextKey = if (repos.isEmpty()) null else page + 1
            val prevKey = if (page == 0) null else page - 1

            LoadResult.Page(repos, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, HomeData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }
    }
}