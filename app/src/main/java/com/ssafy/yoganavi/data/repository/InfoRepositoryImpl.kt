package com.ssafy.yoganavi.data.repository

import com.google.gson.Gson
import com.ssafy.yoganavi.data.source.YogaDetailResponse
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.info.InfoDataSource
import com.ssafy.yoganavi.data.source.lecture.LectureData
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.notice.NoticeData
import com.ssafy.yoganavi.di.IoDispatcher
import com.ssafy.yoganavi.ui.utils.FORBIDDEN
import com.ssafy.yoganavi.ui.utils.NO_AUTH
import com.ssafy.yoganavi.ui.utils.NO_RESPONSE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoRepositoryImpl @Inject constructor(
    private val infoDataSource: InfoDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : InfoRepository {

    override suspend fun getLectureList(): ListResponse<LectureData> {
        val response = withContext(ioDispatcher) { infoDataSource.getLectureList() }
        return response.toListResponse()
    }

    override suspend fun getLecture(recordId: Int): DetailResponse<LectureDetailData> {
        val response = withContext(ioDispatcher) { infoDataSource.getLecture(recordId) }
        return response.toDetailResponse()
    }

    override suspend fun getNoticeList(): ListResponse<NoticeData> {
        val response = withContext(ioDispatcher){infoDataSource.getNoticeList()}
        return response.toListResponse()
    }

    override suspend fun getNotice(articleId: Int): DetailResponse<NoticeData> {
        val response = withContext(ioDispatcher){infoDataSource.getNotice(articleId)}
        return response.toDetailResponse()
    }

    private inline fun <reified T> Response<YogaResponse<T>>.toListResponse(): ListResponse<T> {
        if (code() == FORBIDDEN) return ListResponse.AuthError(message = NO_AUTH)

        body()?.let {
            if (isSuccessful) return ListResponse.Success(it.data, it.message)
            else return ListResponse.Error(it.data, it.message)
        }

        val errorMessage = errorBody()?.let {
            Gson().fromJson(it.charStream(), YogaResponse::class.java)
        }?.message

        return if (errorMessage.isNullOrBlank()) ListResponse.Error(message = NO_RESPONSE)
        else ListResponse.Error(message = errorMessage)
    }

    private inline fun <reified T> Response<YogaDetailResponse<T>>.toDetailResponse(): DetailResponse<T> {
        if (code() == FORBIDDEN) return DetailResponse.AuthError(message = NO_AUTH)

        body()?.let {
            if (isSuccessful) return DetailResponse.Success(it.data, it.message)
            else return DetailResponse.Error(it.data, it.message)
        }

        val errorMessage = errorBody()?.let {
            Gson().fromJson(it.charStream(), YogaDetailResponse::class.java)
        }?.message

        return if (errorMessage.isNullOrBlank()) DetailResponse.Error(message = NO_RESPONSE)
        else DetailResponse.Error(message = errorMessage)
    }
}