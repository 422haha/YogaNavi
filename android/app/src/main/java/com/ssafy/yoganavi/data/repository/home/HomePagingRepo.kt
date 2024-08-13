package com.ssafy.yoganavi.data.repository.home

import androidx.paging.PagingData
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import kotlinx.coroutines.flow.Flow

interface HomePagingRepo {
    fun getHomeList(): Flow<PagingData<HomeData>>
}