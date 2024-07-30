package com.ssafy.yoganavi.data.repository.lecture

import androidx.paging.PagingData
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import kotlinx.coroutines.flow.Flow

interface LectureRepository {

    fun getLectureList(
        sort: String,
        keyword: String? = null,
        searchInTitle: Boolean = true,
        searchInContent: Boolean = true,
    ): Flow<PagingData<LectureData>>

}