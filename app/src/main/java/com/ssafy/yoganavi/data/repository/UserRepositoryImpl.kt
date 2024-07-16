package com.ssafy.yoganavi.data.repository

import com.ssafy.yoganavi.data.ApiResponse
import com.ssafy.yoganavi.data.source.UserDataSource
import com.ssafy.yoganavi.data.source.login.LogInRequest
import com.ssafy.yoganavi.data.source.login.LogInResponse
import com.ssafy.yoganavi.data.source.signup.SignUpRequest
import com.ssafy.yoganavi.data.source.signup.SignUpResponse
import com.ssafy.yoganavi.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    override suspend fun logIn(logInRequest: LogInRequest): ApiResponse<LogInResponse> {
        val response = withContext(ioDispatcher) { userDataSource.logIn(logInRequest) }
        val errorJson = response.errorBody()?.let { JSONObject(it.string()) }
        val error = errorJson?.get("message").toString()

        response.body()?.let {
            if (response.isSuccessful) return ApiResponse.Success(it)
            else return ApiResponse.Error(it.message)
        }

        return if (error.isBlank()) ApiResponse.Error(NO_RESPONSE)
        else ApiResponse.Error(error)
    }

    override suspend fun signUp(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse> {
        val response = withContext(ioDispatcher) { userDataSource.signUp(signUpRequest) }
        val errorJson = response.errorBody()?.let { JSONObject(it.string()) }
        val error = errorJson?.get("message").toString()

        response.body()?.let {
            if (response.isSuccessful) return ApiResponse.Success(it)
            else return ApiResponse.Error(it.message)
        }

        return if (error.isBlank()) ApiResponse.Error(NO_RESPONSE)
        else ApiResponse.Error(error)
    }

    override suspend fun registerEmail(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse> {
        val response = withContext(ioDispatcher) { userDataSource.registerEmail(signUpRequest) }
        val errorJson = response.errorBody()?.let { JSONObject(it.string()) }
        val error = errorJson?.get("message").toString()

        response.body()?.let {
            if (response.isSuccessful) return ApiResponse.Success(it)
            else return ApiResponse.Error(it.message)
        }

        return if (error.isBlank()) ApiResponse.Error(NO_RESPONSE)
        else ApiResponse.Error(error)
    }

    override suspend fun checkAuthEmail(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse> {
        val response = withContext(ioDispatcher) { userDataSource.checkAuthEmail(signUpRequest) }
        val errorJson = response.errorBody()?.let { JSONObject(it.string()) }
        val error = errorJson?.get("message").toString()

        response.body()?.let {
            if (response.isSuccessful) return ApiResponse.Success(it)
            else return ApiResponse.Error(it.message)
        }

        return if (error.isBlank()) ApiResponse.Error(NO_RESPONSE)
        else ApiResponse.Error(error)
    }

    companion object {
        const val NO_RESPONSE = "응답이 없습니다."
    }
}
