package com.ssafy.yoganavi.data.repository.home

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.data.source.home.HomeAPI
import com.ssafy.yoganavi.data.source.home.HomePagingSource
import com.ssafy.yoganavi.di.IoDispatcher
import com.ssafy.yoganavi.ui.utils.PAGE_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomePagingRepoImpl @Inject constructor(
    private val homeAPI: HomeAPI,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): HomePagingRepo {

    override fun getHomeList(): Flow<PagingData<HomeData>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 30,
                maxSize = 90),
            pagingSourceFactory = {
                HomePagingSource(homeAPI)
            }
        ).flow.flowOn(ioDispatcher)
    }
}