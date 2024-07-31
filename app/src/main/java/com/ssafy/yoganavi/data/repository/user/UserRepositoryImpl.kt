package com.ssafy.yoganavi.data.repository.user

import com.ssafy.yoganavi.data.repository.response.DetailResponse
import com.ssafy.yoganavi.data.repository.response.ListResponse
import com.ssafy.yoganavi.data.repository.response.toDetailResponse
import com.ssafy.yoganavi.data.repository.response.toListResponse
import com.ssafy.yoganavi.data.source.user.UserDataSource
import com.ssafy.yoganavi.data.source.user.UserRequest
import com.ssafy.yoganavi.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    override suspend fun logIn(userRequest: UserRequest, fcmToken: String): DetailResponse<Boolean> {
        val response = withContext(ioDispatcher) { userDataSource.logIn(userRequest, fcmToken) }
        return response.toDetailResponse()
    }

    override suspend fun signUp(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.signUp(userRequest) }
        return response.toListResponse()
    }

    override suspend fun registerEmail(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.registerEmail(userRequest) }
        return response.toListResponse()
    }

    override suspend fun checkAuthEmail(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.checkAuthEmail(userRequest) }
        return response.toListResponse()
    }

    override suspend fun findPasswordEmail(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.findPasswordEmail(userRequest) }
        return response.toListResponse()
    }

    override suspend fun checkAuthPassword(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.checkAuthPassword(userRequest) }
        return response.toListResponse()
    }

    override suspend fun registerPassword(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.registerPassword(userRequest) }
        return response.toListResponse()
    }

    override suspend fun isServerOn(): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.isServerOn() }
        return response.toListResponse()
    }

    override suspend fun logout(): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.logout() }
        return response.toListResponse()
    }

    override suspend fun quit(): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.quit() }
        return response.toListResponse()
    }

    override suspend fun updateFcmToken(fcmToken: String): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.updateFcmToken(fcmToken) }
        return response.toListResponse()
    }
}
