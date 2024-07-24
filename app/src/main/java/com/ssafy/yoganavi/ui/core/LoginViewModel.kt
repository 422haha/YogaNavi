package com.ssafy.yoganavi.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.DataStoreRepository
import com.ssafy.yoganavi.data.repository.UserRepository
import com.ssafy.yoganavi.data.repository.response.DetailResponse
import com.ssafy.yoganavi.data.source.user.UserRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _userEvent = MutableSharedFlow<Boolean>()
    val userEvent: SharedFlow<Boolean> = _userEvent.asSharedFlow()

    fun autoLogin() = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            val user = dataStoreRepository.userFlow.first()
            val userRequest = UserRequest(
                email = user.email,
                password = user.password
            )

            val response = userRepository.logIn(userRequest)

            when (response) {
                is DetailResponse.AuthError -> throw RuntimeException()
                is DetailResponse.Error -> throw RuntimeException()
                else -> {}
            }
        }
            .onSuccess { _userEvent.emit(true) }
            .onFailure { _userEvent.emit(false) }
    }
}