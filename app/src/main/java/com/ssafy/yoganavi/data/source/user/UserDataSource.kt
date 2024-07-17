package com.ssafy.yoganavi.data.source.user

import com.ssafy.yoganavi.data.source.YogaResponse
import retrofit2.Response

interface UserDataSource {

    suspend fun logIn(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun signUp(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun registerEmail(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun checkAuthEmail(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun findPasswordEmail(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun checkAuthPassword(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun registerPassword(userRequest: UserRequest): Response<YogaResponse<Unit>>

}
