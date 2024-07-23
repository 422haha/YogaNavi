package com.ssafy.yoganavi.data.source.user

import com.ssafy.yoganavi.data.source.YogaDetailResponse
import com.ssafy.yoganavi.data.source.YogaResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataSourceImpl @Inject constructor(private val userAPI: UserAPI) : UserDataSource {

    override suspend fun logIn(userRequest: UserRequest): Response<YogaDetailResponse<Boolean>> =
        userAPI.login(userRequest.email, userRequest.password)

    override suspend fun signUp(userRequest: UserRequest): Response<YogaResponse<Unit>> =
        userAPI.signUp(userRequest)

    override suspend fun registerEmail(userRequest: UserRequest): Response<YogaResponse<Unit>> =
        userAPI.registerEmail(userRequest)

    override suspend fun checkAuthEmail(userRequest: UserRequest): Response<YogaResponse<Unit>> =
        userAPI.checkAuthEmail(userRequest)

    override suspend fun findPasswordEmail(userRequest: UserRequest): Response<YogaResponse<Unit>> =
        userAPI.findPasswordEmail(userRequest)

    override suspend fun checkAuthPassword(userRequest: UserRequest): Response<YogaResponse<Unit>> =
        userAPI.checkAuthPassword(userRequest)

    override suspend fun registerPassword(userRequest: UserRequest): Response<YogaResponse<Unit>> =
        userAPI.registerPassword(userRequest)
}
