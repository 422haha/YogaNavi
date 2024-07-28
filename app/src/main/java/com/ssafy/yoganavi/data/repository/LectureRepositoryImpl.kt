package com.ssafy.yoganavi.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureAPI
import com.ssafy.yoganavi.data.source.lecture.LecturePagingSource
import com.ssafy.yoganavi.di.IoDispatcher
import com.ssafy.yoganavi.ui.utils.PAGE_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LectureRepositoryImpl @Inject constructor(
    private val lectureAPI: LectureAPI,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LectureRepository {

    override fun getLectureList(
        sort: String,
        keyword: String?,
        searchInTitle: Boolean,
        searchInContent: Boolean,
    ): Flow<PagingData<LectureData>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                LecturePagingSource(
                    lectureAPI = lectureAPI,
                    sort = sort,
                    keyword = keyword,
                    title = searchInTitle,
                    content = searchInContent,
                )
            }
        ).flow.flowOn(ioDispatcher)
    }

}
