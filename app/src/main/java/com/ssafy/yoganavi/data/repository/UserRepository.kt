package com.ssafy.yoganavi.data.repository

import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.user.login.LogInRequest
import com.ssafy.yoganavi.data.source.user.signup.SignUpRequest

interface UserRepository {

    suspend fun logIn(logInRequest: LogInRequest): ApiResponse<YogaResponse<Unit>>

    suspend fun signUp(signUpRequest: SignUpRequest): ApiResponse<YogaResponse<Unit>>

    suspend fun registerEmail(signUpRequest: SignUpRequest): ApiResponse<YogaResponse<Unit>>

    suspend fun checkAuthEmail(signUpRequest: SignUpRequest): ApiResponse<YogaResponse<Unit>>

    suspend fun findPasswordEmail(signUpRequest: SignUpRequest): ApiResponse<YogaResponse<Unit>>

    suspend fun checkAuthPassword(signUpRequest: SignUpRequest): ApiResponse<YogaResponse<Unit>>

    suspend fun registerPassword(signUpRequest: SignUpRequest): ApiResponse<YogaResponse<Unit>>

}
