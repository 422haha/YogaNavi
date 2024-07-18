package com.ssafy.yoganavi.data.repository

import com.ssafy.yoganavi.data.source.user.UserRequest

interface UserRepository {

    suspend fun logIn(userRequest: UserRequest): ApiResponse<Unit>

    suspend fun signUp(userRequest: UserRequest): ApiResponse<Unit>

    suspend fun registerEmail(userRequest: UserRequest): ApiResponse<Unit>

    suspend fun checkAuthEmail(userRequest: UserRequest): ApiResponse<Unit>

    suspend fun findPasswordEmail(userRequest: UserRequest): ApiResponse<Unit>

    suspend fun checkAuthPassword(userRequest: UserRequest): ApiResponse<Unit>

    suspend fun registerPassword(userRequest: UserRequest): ApiResponse<Unit>

}
