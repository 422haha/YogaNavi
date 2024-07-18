package com.ssafy.yoganavi.ui.loginUI.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.ApiResponse
import com.ssafy.yoganavi.data.repository.UserRepository
import com.ssafy.yoganavi.data.source.user.UserRequest
import com.ssafy.yoganavi.ui.utils.IS_BLANK
import com.ssafy.yoganavi.ui.utils.NO_RESPONSE
import com.ssafy.yoganavi.ui.utils.isBlank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginEvent: MutableSharedFlow<LogInEvent<Unit>> = MutableSharedFlow()
    val loginEvent: SharedFlow<LogInEvent<Unit>> = _loginEvent.asSharedFlow()

    fun login(email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        if (arrayOf(email, password).isBlank()) {
            emitError(IS_BLANK)
            return@launch
        }

        val request = UserRequest(email = email, password = password)
        runCatching { userRepository.logIn(request) }
            .onSuccess { emitResponse(it) }
            .onFailure { emitError(NO_RESPONSE) }
    }

    private suspend fun emitResponse(response: ApiResponse<Unit>) = when (response) {
        is ApiResponse.Success -> emitSuccess(response)
        is ApiResponse.Error -> emitError(response.message)
    }

    private suspend fun emitSuccess(response: ApiResponse.Success<Unit>) =
        _loginEvent.emit(LogInEvent.LoginSuccess(response.data, response.message))

    private suspend fun emitError(message: String) =
        _loginEvent.emit(LogInEvent.LoginError(message = message))
}
