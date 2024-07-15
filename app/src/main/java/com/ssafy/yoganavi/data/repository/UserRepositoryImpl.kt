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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    override suspend fun logIn(logInRequest: LogInRequest): ApiResponse<LogInResponse> {
        val response = withContext(ioDispatcher) {
            userDataSource.logIn(logInRequest)
        }

        response.body()?.let {
            if (response.isSuccessful) return ApiResponse.Success(it)
            else return ApiResponse.Error(response.message())
        } ?: return ApiResponse.Error(response.message())
    }

    override suspend fun signUp(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse> {
        val response = withContext(ioDispatcher) {
            userDataSource.signUp(signUpRequest)
        }

        response.body()?.let {
            if (response.isSuccessful) return ApiResponse.Success(it)
            else return ApiResponse.Error(response.message())
        } ?: return ApiResponse.Error(response.message())
    }
}
