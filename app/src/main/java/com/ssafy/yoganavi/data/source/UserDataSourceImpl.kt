package com.ssafy.yoganavi.data.source

import com.ssafy.yoganavi.data.source.login.LogInRequest
import com.ssafy.yoganavi.data.source.login.LogInResponse
import com.ssafy.yoganavi.data.source.signup.SignUpRequest
import com.ssafy.yoganavi.data.source.signup.SignUpResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataSourceImpl @Inject constructor(private val userAPI: UserAPI) : UserDataSource {
    private val mutex = Mutex()

    override suspend fun logIn(logInRequest: LogInRequest): Response<LogInResponse> =
        mutex.withLock {
            userAPI.login(logInRequest)
        }

    override suspend fun signUp(signUpRequest: SignUpRequest): Response<SignUpResponse> =
        mutex.withLock {
            userAPI.signUp(signUpRequest)
        }
}
