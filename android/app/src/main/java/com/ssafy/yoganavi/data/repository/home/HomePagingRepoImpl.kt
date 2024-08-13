package com.ssafy.yoganavi.data.repository.home

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.data.source.home.HomeAPI
import com.ssafy.yoganavi.data.source.home.HomePagingSource
import com.ssafy.yoganavi.di.IoDispatcher
import com.ssafy.yoganavi.ui.utils.HOME_PAGE_SIZE
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
            config = PagingConfig(pageSize = HOME_PAGE_SIZE,
                enablePlaceholders = false),
            pagingSourceFactory = {
                HomePagingSource(homeAPI)
            }
        ).flow.flowOn(ioDispatcher)
    }
}