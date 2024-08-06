package com.ssafy.yoganavi.data.source.user

import com.ssafy.yoganavi.data.source.response.YogaDetailResponse
import com.ssafy.yoganavi.data.source.response.YogaResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataSourceImpl @Inject constructor(private val userAPI: UserAPI) : UserDataSource {

    override suspend fun logIn(
        userRequest: UserRequest,
        fcmToken: String
    ): Response<YogaDetailResponse<Boolean>> =
        userAPI.login(userRequest.email, userRequest.password, fcmToken)

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

    override suspend fun isServerOn(): Response<YogaResponse<Unit>> =
        userAPI.isServerOn()

    override suspend fun logout(): Response<YogaResponse<Unit>> =
        userAPI.logout()

    override suspend fun quit(): Response<YogaResponse<Unit>> =
        userAPI.quit()

    override suspend fun updateFcmToken(fcmToken: String): Response<YogaResponse<Unit>> =
        userAPI.updateFcmToken(fcmToken)

    override suspend fun checkPassword(password: HashMap<String, String>): Response<YogaDetailResponse<Boolean>> =
        userAPI.checkPassword(password)
}
