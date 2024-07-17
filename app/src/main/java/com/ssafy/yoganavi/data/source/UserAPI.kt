package com.ssafy.yoganavi.data.source

import com.ssafy.yoganavi.data.source.login.LogInResponse
import com.ssafy.yoganavi.data.source.signup.SignUpRequest
import com.ssafy.yoganavi.data.source.signup.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserAPI {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LogInResponse>

    @POST("members/register")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @POST("members/register/email")
    suspend fun registerEmail(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @POST("members/register/authnumber")
    suspend fun checkAuthEmail(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @POST("members/find-password/email")
    suspend fun findPasswordEmail(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @POST("members/find-password/authnumber")
    suspend fun checkAuthPassword(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @POST("members/find-password")
    suspend fun registerPassword(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

}
