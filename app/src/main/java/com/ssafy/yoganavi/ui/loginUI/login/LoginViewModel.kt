package com.ssafy.yoganavi.ui.loginUI.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.ApiResponse
import com.ssafy.yoganavi.data.repository.UserRepository
import com.ssafy.yoganavi.data.source.login.LogInRequest
import com.ssafy.yoganavi.data.source.login.LogInResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginEvent: MutableSharedFlow<ApiResponse<LogInResponse>> = MutableSharedFlow()
    val loginEvent: SharedFlow<ApiResponse<LogInResponse>> = _loginEvent.asSharedFlow()

    fun login(email: String, password: String) = viewModelScope.launch {
        val request = LogInRequest(email, password)
        runCatching { userRepository.logIn(request) }
            .onSuccess { _loginEvent.emit(it) }
            .onFailure { _loginEvent.emit(ApiResponse.Error(NO_RESPONSE)) }
    }

    companion object {
        const val NO_RESPONSE = "에러 발생"
    }
}
