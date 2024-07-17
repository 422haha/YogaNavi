package com.ssafy.yoganavi.data.source.user

import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.user.login.LogInRequest
import com.ssafy.yoganavi.data.source.user.signup.SignUpRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataSourceImpl @Inject constructor(private val userAPI: UserAPI) : UserDataSource {

    override suspend fun logIn(logInRequest: LogInRequest): Response<YogaResponse<Unit>> =
        userAPI.login(logInRequest.username, logInRequest.password)

    override suspend fun signUp(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>> =
        userAPI.signUp(signUpRequest)

    override suspend fun registerEmail(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>> =
        userAPI.registerEmail(signUpRequest)

    override suspend fun checkAuthEmail(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>> =
        userAPI.checkAuthEmail(signUpRequest)

    override suspend fun findPasswordEmail(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>> =
        userAPI.findPasswordEmail(signUpRequest)

    override suspend fun checkAuthPassword(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>> =
        userAPI.checkAuthPassword(signUpRequest)

    override suspend fun registerPassword(signUpRequest: SignUpRequest): Response<YogaResponse<Unit>> =
        userAPI.registerPassword(signUpRequest)
}
