package com.ssafy.yoganavi.data.source.user

import com.ssafy.yoganavi.data.source.response.YogaDetailResponse
import com.ssafy.yoganavi.data.source.response.YogaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserAPI {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Header("FCM-TOKEN") token: String
    ): Response<YogaDetailResponse<Boolean>>

    @POST("members/register")
    suspend fun signUp(@Body userRequest: UserRequest): Response<YogaResponse<Unit>>

    @POST("members/register/email")
    suspend fun registerEmail(@Body userRequest: UserRequest): Response<YogaResponse<Unit>>

    @POST("members/register/authnumber")
    suspend fun checkAuthEmail(@Body userRequest: UserRequest): Response<YogaResponse<Unit>>

    @POST("members/find-password/email")
    suspend fun findPasswordEmail(@Body userRequest: UserRequest): Response<YogaResponse<Unit>>

    @POST("members/find-password/authnumber")
    suspend fun checkAuthPassword(@Body userRequest: UserRequest): Response<YogaResponse<Unit>>

    @POST("members/find-password")
    suspend fun registerPassword(@Body userRequest: UserRequest): Response<YogaResponse<Unit>>

    @POST("is-on")
    suspend fun isServerOn(): Response<YogaResponse<Unit>>

    @POST("mylogout")
    suspend fun logout(): Response<YogaResponse<Unit>>

    @POST("delete")
    suspend fun quit(): Response<YogaResponse<Unit>>

    @PUT("fcm")
    suspend fun updateFcmToken(@Header("FCM-TOKEN") fcmToken: String): Response<YogaResponse<Unit>>

    @POST("mypage/check")
    suspend fun checkPassword(@Body body: String): Response<YogaDetailResponse<Boolean>>
}
