package com.ssafy.yoganavi.data.repository

import com.ssafy.yoganavi.data.ApiResponse
import com.ssafy.yoganavi.data.source.login.LogInRequest
import com.ssafy.yoganavi.data.source.login.LogInResponse
import com.ssafy.yoganavi.data.source.signup.SignUpRequest
import com.ssafy.yoganavi.data.source.signup.SignUpResponse

interface UserRepository {
    suspend fun logIn(logInRequest: LogInRequest): ApiResponse<LogInResponse>
    suspend fun signUp(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse>
    suspend fun registerEmail(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse>
    suspend fun checkAuthEmail(signUpRequest: SignUpRequest): ApiResponse<SignUpResponse>
}
