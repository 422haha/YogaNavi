package com.ssafy.yoganavi.ui.loginUI.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.ApiResponse
import com.ssafy.yoganavi.data.repository.UserRepository
import com.ssafy.yoganavi.data.source.user.signup.SignUpRequest
import com.ssafy.yoganavi.ui.utils.IS_BLANK
import com.ssafy.yoganavi.ui.utils.NO_RESPONSE
import com.ssafy.yoganavi.ui.utils.PASSWORD_DIFF
import com.ssafy.yoganavi.ui.utils.isBlank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _joinEvent: MutableSharedFlow<JoinEvent<Unit>> = MutableSharedFlow()
    val joinEvent: SharedFlow<JoinEvent<Unit>> = _joinEvent.asSharedFlow()

    fun registerEmail(email: String) = viewModelScope.launch(Dispatchers.IO) {
        if (arrayOf(email).isBlank()) {
            emitError(IS_BLANK)
            return@launch
        }

        val signUpRequest = SignUpRequest(email = email)
        runCatching { userRepository.registerEmail(signUpRequest) }
            .onSuccess { emitResponse(it, JoinEvent.RegisterEmailSuccess::class.java) }
            .onFailure { emitError(NO_RESPONSE) }
    }

    fun checkAuthEmail(checkNumber: Int?) = viewModelScope.launch(Dispatchers.IO) {
        checkNumber?.let {
            val signUpRequest = SignUpRequest(authnumber = checkNumber)
            runCatching { userRepository.checkAuthEmail(signUpRequest) }
                .onSuccess { emitResponse(it, JoinEvent.CheckEmailSuccess::class.java) }
                .onFailure { emitError(NO_RESPONSE) }
        } ?: emitError(IS_BLANK)
    }

    fun signUp(
        email: String,
        password: String,
        passwordAgain: String,
        nickname: String,
        isTeacher: Boolean
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (arrayOf(email, password, nickname).isBlank()) {
            emitError(IS_BLANK)
            return@launch
        }

        if (password != passwordAgain) {
            emitError(PASSWORD_DIFF)
            return@launch
        }

        val signUpRequest = SignUpRequest(
            email = email,
            password = password,
            nickname = nickname,
            teacher = isTeacher
        )

        runCatching { userRepository.signUp(signUpRequest) }
            .onSuccess { emitResponse(it, JoinEvent.SignUpSuccess::class.java) }
            .onFailure { emitError(NO_RESPONSE) }
    }

    private suspend fun emitResponse(
        response: ApiResponse<Unit>,
        type: Class<out JoinEvent<*>>
    ) = when (response) {
        is ApiResponse.Success -> emitSuccess(response, type)
        is ApiResponse.Error -> emitError(response.message)
    }

    private suspend fun emitSuccess(response: ApiResponse<Unit>, type: Class<out JoinEvent<*>>) =
        when (type) {
            JoinEvent.RegisterEmailSuccess::class.java -> {
                _joinEvent.emit(JoinEvent.RegisterEmailSuccess(response.data, response.message))
            }

            JoinEvent.CheckEmailSuccess::class.java -> {
                _joinEvent.emit(JoinEvent.CheckEmailSuccess(response.data, response.message))
            }

            JoinEvent.SignUpSuccess::class.java -> {
                _joinEvent.emit(JoinEvent.SignUpSuccess(response.data, response.message))
            }

            else -> {
                emitError(NO_RESPONSE)
            }
        }

    private suspend fun emitError(message: String) =
        _joinEvent.emit(JoinEvent.Error(message = message))

}
