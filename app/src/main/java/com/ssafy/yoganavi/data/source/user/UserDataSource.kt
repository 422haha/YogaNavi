package com.ssafy.yoganavi.data.source.user

import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.user.login.LogInRequest
import com.ssafy.yoganavi.data.source.user.signup.SignUpRequest
import retrofit2.Response

interface UserDataSource {

    suspend fun logIn(logInRequest: LogInRequest): Response<YogaResponse<Unit>>

    suspend fun signUp(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

    suspend fun registerEmail(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

    suspend fun checkAuthEmail(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

    suspend fun findPasswordEmail(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

    suspend fun checkAuthPassword(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

    suspend fun registerPassword(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

}
