package com.ssafy.yoganavi.data.repository

import com.google.gson.Gson
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.user.UserDataSource
import com.ssafy.yoganavi.data.source.user.UserRequest
import com.ssafy.yoganavi.di.IoDispatcher
import com.ssafy.yoganavi.ui.utils.NO_RESPONSE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    override suspend fun logIn(userRequest: UserRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.logIn(userRequest) }
        return response.toApiResponse()
    }

    override suspend fun signUp(userRequest: UserRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.signUp(userRequest) }
        return response.toApiResponse()
    }

    override suspend fun registerEmail(userRequest: UserRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.registerEmail(userRequest) }
        return response.toApiResponse()
    }

    override suspend fun checkAuthEmail(userRequest: UserRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.checkAuthEmail(userRequest) }
        return response.toApiResponse()
    }

    override suspend fun findPasswordEmail(userRequest: UserRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.findPasswordEmail(userRequest) }
        return response.toApiResponse()
    }

    override suspend fun checkAuthPassword(userRequest: UserRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.checkAuthPassword(userRequest) }
        return response.toApiResponse()
    }

    override suspend fun registerPassword(userRequest: UserRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.registerPassword(userRequest) }
        return response.toApiResponse()
    }

    private fun Response<YogaResponse<Unit>>.toApiResponse(): ApiResponse<Unit> {
        body()?.let {
            if (isSuccessful) return ApiResponse.Success(it.data, it.message)
            else return ApiResponse.Error(it.data, it.message)
        }

        val gson = errorBody()?.let { Gson().fromJson(it.charStream(), YogaResponse::class.java) }
        val errorMessage = gson?.message
        return if (errorMessage.isNullOrBlank()) ApiResponse.Error(message = NO_RESPONSE)
        else ApiResponse.Error(message = errorMessage)
    }
}
