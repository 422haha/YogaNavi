package com.ssafy.yoganavi.data.repository

import com.google.gson.Gson
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.user.UserDataSource
import com.ssafy.yoganavi.data.source.user.login.LogInRequest
import com.ssafy.yoganavi.data.source.user.signup.SignUpRequest
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

    override suspend fun logIn(logInRequest: LogInRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.logIn(logInRequest) }
        return response.toApiResponse()
    }

    override suspend fun signUp(signUpRequest: SignUpRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.signUp(signUpRequest) }
        return response.toApiResponse()
    }

    override suspend fun registerEmail(signUpRequest: SignUpRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.registerEmail(signUpRequest) }
        return response.toApiResponse()
    }

    override suspend fun checkAuthEmail(signUpRequest: SignUpRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.checkAuthEmail(signUpRequest) }
        return response.toApiResponse()
    }

    override suspend fun findPasswordEmail(signUpRequest: SignUpRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.findPasswordEmail(signUpRequest) }
        return response.toApiResponse()
    }

    override suspend fun checkAuthPassword(signUpRequest: SignUpRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.checkAuthPassword(signUpRequest) }
        return response.toApiResponse()
    }

    override suspend fun registerPassword(signUpRequest: SignUpRequest): ApiResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.registerPassword(signUpRequest) }
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
