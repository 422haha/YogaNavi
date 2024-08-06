package com.ssafy.yoganavi.data.source.user

import com.ssafy.yoganavi.data.source.response.YogaDetailResponse
import com.ssafy.yoganavi.data.source.response.YogaResponse
import retrofit2.Response

interface UserDataSource {

    suspend fun logIn(
        userRequest: UserRequest,
        fcmToken: String
    ): Response<YogaDetailResponse<Boolean>>

    suspend fun signUp(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun registerEmail(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun checkAuthEmail(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun findPasswordEmail(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun checkAuthPassword(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun registerPassword(userRequest: UserRequest): Response<YogaResponse<Unit>>

    suspend fun isServerOn(): Response<YogaResponse<Unit>>

    suspend fun logout(): Response<YogaResponse<Unit>>

    suspend fun quit(): Response<YogaResponse<Unit>>

    suspend fun updateFcmToken(fcmToken: String): Response<YogaResponse<Unit>>

    suspend fun checkPassword(password: HashMap<String, String>): Response<YogaDetailResponse<Boolean>>
}
