package com.ssafy.yoganavi.data.source

import com.ssafy.yoganavi.data.source.login.LogInRequest
import com.ssafy.yoganavi.data.source.login.LogInResponse
import com.ssafy.yoganavi.data.source.signup.SignUpRequest
import com.ssafy.yoganavi.data.source.signup.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("login")
    suspend fun login(@Body logInRequest: LogInRequest): Response<LogInResponse>

    @POST("signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

}
