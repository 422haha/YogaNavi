package com.ssafy.yoganavi.ui.loginUI.join

import android.os.CountDownTimer
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.response.ListResponse
import com.ssafy.yoganavi.data.repository.user.UserRepository
import com.ssafy.yoganavi.data.source.user.UserRequest
import com.ssafy.yoganavi.ui.utils.END_TIME
import com.ssafy.yoganavi.ui.utils.HAS_SPACE
import com.ssafy.yoganavi.ui.utils.IS_BLANK
import com.ssafy.yoganavi.ui.utils.IS_NOT_EMAIL
import com.ssafy.yoganavi.ui.utils.NO_RESPONSE
import com.ssafy.yoganavi.ui.utils.PASSWORD_DIFF
import com.ssafy.yoganavi.ui.utils.TIME_OUT
import com.ssafy.yoganavi.ui.utils.isBlank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _joinEvent: MutableSharedFlow<JoinEvent<Unit>> = MutableSharedFlow()
    val joinEvent: SharedFlow<JoinEvent<Unit>> = _joinEvent.asSharedFlow()

    private val _timeFlow: MutableStateFlow<String> = MutableStateFlow("")
    val timeFlow: StateFlow<String> = _timeFlow.asStateFlow()

    private val timer = object : CountDownTimer(TIME_OUT * 60, 1000) {
        override fun onTick(time: Long) {
            val secondsRemaining = time / 1000
            val minutes = secondsRemaining / 60
            val seconds = secondsRemaining % 60
            val remainTime = String.format(Locale.KOREA, "%02d:%02d", minutes, seconds)
            _timeFlow.value = remainTime
        }

        override fun onFinish() {
            _timeFlow.value = END_TIME
        }
    }

    fun registerEmail(email: String) = viewModelScope.launch(Dispatchers.IO) {
        if (arrayOf(email).isBlank()) {
            emitError(IS_BLANK)
            return@launch
        }

        if(!isEmail(email)){
            emitError(IS_NOT_EMAIL)
            return@launch
        }

        val userRequest = UserRequest(email = email)
        runCatching { userRepository.registerEmail(userRequest) }
            .onSuccess { emitResponse(it, JoinEvent.RegisterEmailSuccess::class.java) }
            .onFailure { emitError(NO_RESPONSE) }
    }

    fun checkAuthEmail(email: String, checkNumber: Int?) = viewModelScope.launch(Dispatchers.IO) {
        if(!isEmail(email)){
            emitError(IS_NOT_EMAIL)
            return@launch
        }

        checkNumber?.let {
            val userRequest = UserRequest(email = email, authnumber = checkNumber)
            runCatching { userRepository.checkAuthEmail(userRequest) }
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

        if(!isEmail(email)){
            emitError(IS_NOT_EMAIL)
            return@launch
        }

        if(hasSpace(password)){
            emitError(HAS_SPACE)
            return@launch
        }

        if (password != passwordAgain) {
            emitError(PASSWORD_DIFF)
            return@launch
        }

        val userRequest = UserRequest(
            email = email,
            password = password,
            nickname = nickname,
            teacher = isTeacher
        )

        runCatching { userRepository.signUp(userRequest) }
            .onSuccess { emitResponse(it, JoinEvent.SignUpSuccess::class.java) }
            .onFailure { emitError(NO_RESPONSE) }
    }

    private suspend fun emitResponse(
        response: ListResponse<Unit>,
        type: Class<out JoinEvent<*>>
    ) = when (response) {
        is ListResponse.Success -> emitSuccess(response, type)
        is ListResponse.Error -> emitError(response.message)
        is ListResponse.AuthError -> emitError(response.message)
    }

    private suspend fun emitSuccess(response: ListResponse<Unit>, type: Class<out JoinEvent<*>>) =
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

    private fun isEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun hasSpace(input: String): Boolean {
        val regex = "\\s".toRegex()
        return regex.containsMatchIn(input)
    }

    fun timerStart(): CountDownTimer = timer.start()

    fun timerEnd() = timer.cancel()
}
