package com.ssafy.yoganavi.data.repository

import com.ssafy.yoganavi.data.source.user.UserRequest

interface UserRepository {

    suspend fun logIn(userRequest: UserRequest): ListResponse<Unit>

    suspend fun signUp(userRequest: UserRequest): ListResponse<Unit>

    suspend fun registerEmail(userRequest: UserRequest): ListResponse<Unit>

    suspend fun checkAuthEmail(userRequest: UserRequest): ListResponse<Unit>

    suspend fun findPasswordEmail(userRequest: UserRequest): ListResponse<Unit>

    suspend fun checkAuthPassword(userRequest: UserRequest): ListResponse<Unit>

    suspend fun registerPassword(userRequest: UserRequest): ListResponse<Unit>

}
