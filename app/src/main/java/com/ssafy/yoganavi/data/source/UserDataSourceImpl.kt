package com.ssafy.yoganavi.data.source

import com.ssafy.yoganavi.data.source.login.LogInRequest
import com.ssafy.yoganavi.data.source.login.LogInResponse
import com.ssafy.yoganavi.data.source.signup.SignUpRequest
import com.ssafy.yoganavi.data.source.signup.SignUpResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataSourceImpl @Inject constructor(private val userAPI: UserAPI) : UserDataSource {

    override suspend fun logIn(logInRequest: LogInRequest): Response<LogInResponse> =
        userAPI.login(logInRequest.username, logInRequest.password)

    override suspend fun signUp(signUpRequest: SignUpRequest): Response<SignUpResponse> =
        userAPI.signUp(signUpRequest)

    override suspend fun registerEmail(signUpRequest: SignUpRequest): Response<SignUpResponse> =
        userAPI.registerEmail(signUpRequest)

    override suspend fun checkAuthEmail(signUpRequest: SignUpRequest): Response<SignUpResponse> =
        userAPI.checkAuthEmail(signUpRequest)

    override suspend fun findPasswordEmail(signUpRequest: SignUpRequest): Response<SignUpResponse> =
        userAPI.findPasswordEmail(signUpRequest)

    override suspend fun checkAuthPassword(signUpRequest: SignUpRequest): Response<SignUpResponse> =
        userAPI.checkAuthPassword(signUpRequest)

    override suspend fun registerPassword(signUpRequest: SignUpRequest): Response<SignUpResponse> =
        userAPI.registerPassword(signUpRequest)
}
