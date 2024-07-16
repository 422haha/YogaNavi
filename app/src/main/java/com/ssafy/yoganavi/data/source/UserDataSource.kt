package com.ssafy.yoganavi.data.source

import com.ssafy.yoganavi.data.source.login.LogInRequest
import com.ssafy.yoganavi.data.source.login.LogInResponse
import com.ssafy.yoganavi.data.source.signup.SignUpRequest
import com.ssafy.yoganavi.data.source.signup.SignUpResponse
import retrofit2.Response

interface UserDataSource {
    suspend fun logIn(logInRequest: LogInRequest): Response<LogInResponse>
    suspend fun signUp(signUpRequest: SignUpRequest): Response<SignUpResponse>
    suspend fun registerEmail(signUpRequest: SignUpRequest): Response<SignUpResponse>
    suspend fun checkAuthEmail(signUpRequest: SignUpRequest): Response<SignUpResponse>
}
