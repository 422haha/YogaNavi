package com.ssafy.yoganavi.data.repository.response

import com.google.gson.Gson
import com.ssafy.yoganavi.data.source.response.YogaDetailResponse
import com.ssafy.yoganavi.data.source.response.YogaResponse
import com.ssafy.yoganavi.ui.utils.FORBIDDEN
import com.ssafy.yoganavi.ui.utils.NO_RESPONSE
import com.ssafy.yoganavi.ui.utils.UNAUTHORIZED
import retrofit2.Response

inline fun <reified T> Response<YogaResponse<T>>.toListResponse(): ListResponse<T> {
    if (code() == FORBIDDEN || code() == UNAUTHORIZED) throw AuthException()

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

inline fun <reified T> Response<YogaDetailResponse<T>>.toDetailResponse(): DetailResponse<T> {
    if (code() == FORBIDDEN || code() == UNAUTHORIZED) throw AuthException()

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

inline fun <reified T> Response<YogaResponse<T>>.toUserListResponse(): ListResponse<T> {
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

inline fun <reified T> Response<YogaDetailResponse<T>>.toUserDetailResponse(): DetailResponse<T> {
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
