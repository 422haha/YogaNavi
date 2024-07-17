package com.ssafy.yoganavi.data.repository

import com.ssafy.yoganavi.data.ApiResponse
import com.ssafy.yoganavi.data.source.UserDataSource
import com.ssafy.yoganavi.data.source.login.LogInRequest
import com.ssafy.yoganavi.data.source.login.LogInResponse
import com.ssafy.yoganavi.data.source.signup.SignUpRequest
import com.ssafy.yoganavi.data.source.signup.SignUpResponse
import com.ssafy.yoganavi.di.IoDispatcher
import com.ssafy.yoganavi.ui.utils.NO_RESPONSE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    override suspend fun logIn(logInRequest: LogInRequest): ApiResponse<LogInResponse> {
        val response = withContext(ioDispatcher) { userDataSource.logIn(logInRequest) }
        return response.toApiResponse()
    }

    override suspend fun signUp(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse> {
        val response = withContext(ioDispatcher) { userDataSource.signUp(signUpRequest) }
        return response.toApiResponse()
    }

    override suspend fun registerEmail(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse> {
        val response = withContext(ioDispatcher) { userDataSource.registerEmail(signUpRequest) }
        return response.toApiResponse()
    }

    override suspend fun checkAuthEmail(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse> {
        val response = withContext(ioDispatcher) { userDataSource.checkAuthEmail(signUpRequest) }
        return response.toApiResponse()
    }

    override suspend fun findPasswordEmail(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse> {
        val response = withContext(ioDispatcher) { userDataSource.findPasswordEmail(signUpRequest) }
        return response.toApiResponse()
    }

    override suspend fun checkAuthPassword(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse> {
        val response = withContext(ioDispatcher) { userDataSource.checkAuthPassword(signUpRequest) }
        return response.toApiResponse()
    }

    override suspend fun registerPassword(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse> {
        val response = withContext(ioDispatcher) { userDataSource.registerPassword(signUpRequest) }
        return response.toApiResponse()
    }

    @JvmName("LogIn")
    private fun Response<LogInResponse>.toApiResponse(): ApiResponse<LogInResponse> {
        val errorJson = errorBody()?.let { JSONObject(it.string()) }
        val error = errorJson?.get("message").toString()

        body()?.let {
            if (isSuccessful) return ApiResponse.Success(it)
            else return ApiResponse.Error(it.message)
        }

        return if (error.isBlank()) ApiResponse.Error(NO_RESPONSE)
        else ApiResponse.Error(error)
    }

    @JvmName("SignUp")
    private fun Response<SignUpResponse>.toApiResponse(): ApiResponse<SignUpResponse> {
        val errorJson = errorBody()?.let { JSONObject(it.string()) }
        val error = errorJson?.get("message").toString()

        body()?.let {
            if (isSuccessful) return ApiResponse.Success(it)
            else return ApiResponse.Error(it.message)
        }

        return if (error.isBlank()) ApiResponse.Error(NO_RESPONSE)
        else ApiResponse.Error(error)
    }
}
