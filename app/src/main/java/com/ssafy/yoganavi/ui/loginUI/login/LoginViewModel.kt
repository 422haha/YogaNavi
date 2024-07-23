package com.ssafy.yoganavi.ui.loginUI.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.DetailResponse
import com.ssafy.yoganavi.data.repository.UserRepository
import com.ssafy.yoganavi.data.source.user.UserRequest
import com.ssafy.yoganavi.ui.utils.IS_BLANK
import com.ssafy.yoganavi.ui.utils.NO_AUTH
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

    private val _loginEvent: MutableSharedFlow<LogInEvent> = MutableSharedFlow()
    val loginEvent: SharedFlow<LogInEvent> = _loginEvent.asSharedFlow()

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

    private suspend fun emitResponse(response: DetailResponse<Boolean>) = when (response) {
        is DetailResponse.Success -> emitSuccess(response)
        is DetailResponse.Error -> emitError(NO_RESPONSE)
        is DetailResponse.AuthError -> emitError(NO_AUTH)
    }

    private suspend fun emitSuccess(response: DetailResponse.Success<Boolean>) =
        response.data?.let { _loginEvent.emit(LogInEvent.LoginSuccess(it, response.message)) }

    private suspend fun emitError(message: String) =
        _loginEvent.emit(LogInEvent.LoginError(message = message))
}
