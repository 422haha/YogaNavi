package com.ssafy.yoganavi.ui.loginUI.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.ApiResponse
import com.ssafy.yoganavi.data.repository.UserRepository
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.user.login.LogInRequest
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

    private val _loginEvent: MutableSharedFlow<LogInEvent<YogaResponse<Unit>>> = MutableSharedFlow()
    val loginEvent: SharedFlow<LogInEvent<YogaResponse<Unit>>> = _loginEvent.asSharedFlow()

    fun login(email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        if (arrayOf(email, password).isBlank()) {
            emitError(IS_BLANK)
            return@launch
        }

        val request = LogInRequest(email, password)
        runCatching { userRepository.logIn(request) }
            .onSuccess { emitResponse(it, LogInEvent.LoginSuccess::class.java) }
            .onFailure { _loginEvent.emit(LogInEvent.LoginError(NO_RESPONSE)) }
    }

    private suspend fun emitResponse(
        response: ApiResponse<YogaResponse<Unit>>,
        type: Class<out LogInEvent<*>>
    ) = when (response) {
        is ApiResponse.Success -> response.data?.let { emitSuccess(it, type) }
        is ApiResponse.Error -> emitError(response.message.toString())
    }

    private suspend fun emitSuccess(data: YogaResponse<Unit>, type: Class<out LogInEvent<*>>) =
        when (type) {
            LogInEvent.LoginSuccess::class.java -> {
                _loginEvent.emit(LogInEvent.LoginSuccess(data))
            }

            else -> {
                emitError(NO_RESPONSE)
            }
        }

    private suspend fun emitError(message: String) =
        _loginEvent.emit(LogInEvent.LoginError(message))
}
