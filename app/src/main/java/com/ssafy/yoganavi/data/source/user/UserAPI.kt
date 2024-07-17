package com.ssafy.yoganavi.data.source.user

import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.user.signup.SignUpRequest
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
    ): Response<YogaResponse<Unit>>

    @POST("members/register")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

    @POST("members/register/email")
    suspend fun registerEmail(@Body signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

    @POST("members/register/authnumber")
    suspend fun checkAuthEmail(@Body signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

    @POST("members/find-password/email")
    suspend fun findPasswordEmail(@Body signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

    @POST("members/find-password/authnumber")
    suspend fun checkAuthPassword(@Body signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

    @POST("members/find-password")
    suspend fun registerPassword(@Body signUpRequest: SignUpRequest): Response<YogaResponse<Unit>>

}
