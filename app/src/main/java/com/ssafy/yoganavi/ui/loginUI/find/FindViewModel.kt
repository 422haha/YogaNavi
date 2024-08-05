package com.ssafy.yoganavi.ui.loginUI.find

import android.os.CountDownTimer
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.response.ListResponse
import com.ssafy.yoganavi.data.repository.user.UserRepository
import com.ssafy.yoganavi.data.source.user.UserRequest
import com.ssafy.yoganavi.ui.utils.END_TIME
import com.ssafy.yoganavi.ui.utils.IS_BLANK
import com.ssafy.yoganavi.ui.utils.IS_NOT_EMAIL
import com.ssafy.yoganavi.ui.utils.NOT_USER
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
class FindViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _findPasswordEvent: MutableSharedFlow<FindEvent<Unit>> = MutableSharedFlow()
    val findPasswordEvent: SharedFlow<FindEvent<Unit>> = _findPasswordEvent.asSharedFlow()

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

    fun sendEmail(email: String) = viewModelScope.launch(Dispatchers.IO) {
        if (arrayOf(email).isBlank()) {
            emitError(IS_BLANK)
            return@launch
        }

        if(!isEmail(email)){
            emitError(IS_NOT_EMAIL)
            return@launch
        }

        val userRequest = UserRequest(email = email)
        runCatching { userRepository.findPasswordEmail(userRequest) }
            .onSuccess { emitResponse(it, FindEvent.SendEmailSuccess::class.java) }
            .onFailure { emitError(NO_RESPONSE) }
    }

    fun checkAuthEmail(email: String, checkNumber: Int?) = viewModelScope.launch(Dispatchers.IO) {
        if(!isEmail(email)){
            emitError(IS_NOT_EMAIL)
            return@launch
        }

        checkNumber?.let {
            val userRequest = UserRequest(email = email, authnumber = checkNumber)
            runCatching { userRepository.checkAuthPassword(userRequest) }
                .onSuccess { emitResponse(it, FindEvent.CheckEmailSuccess::class.java) }
                .onFailure { emitError(NO_RESPONSE) }
        } ?: emitError(IS_BLANK)
    }

    fun registerNewPassword(email: String, password: String, passwordAgain: String) =
        viewModelScope.launch(Dispatchers.IO) {
            if (arrayOf(email, password).isBlank()) {
                emitError(IS_BLANK)
                return@launch
            }

            if(!isEmail(email)){
                emitError(IS_NOT_EMAIL)
                return@launch
            }

            if (password != passwordAgain) {
                emitError(PASSWORD_DIFF)
                return@launch
            }

            val userRequest = UserRequest(email = email, password = password)
            runCatching { userRepository.registerPassword(userRequest) }
                .onSuccess { emitResponse(it, FindEvent.RegisterPasswordSuccess::class.java) }
                .onFailure { emitError(NO_RESPONSE) }
        }

    private suspend fun emitResponse(response: ListResponse<Unit>, type: Class<out FindEvent<*>>) =
        when (response) {
            is ListResponse.Success -> emitSuccess(response, type)
            is ListResponse.Error -> emitError(response.message)
            is ListResponse.AuthError -> emitError(response.message)
        }

    private suspend fun emitSuccess(response: ListResponse<Unit>, type: Class<out FindEvent<*>>) =
        when (type) {
            FindEvent.SendEmailSuccess::class.java -> {
                if(response.message == NOT_USER) emitError(NOT_USER)
                else _findPasswordEvent.emit(
                    FindEvent.SendEmailSuccess(response.data, response.message)
                )
            }

            FindEvent.CheckEmailSuccess::class.java -> {
                _findPasswordEvent.emit(
                    FindEvent.CheckEmailSuccess(response.data, response.message)
                )
            }

            FindEvent.RegisterPasswordSuccess::class.java -> {
                _findPasswordEvent.emit(
                    FindEvent.RegisterPasswordSuccess(response.data, response.message)
                )
            }

            else -> {
                emitError(NO_RESPONSE)
            }
        }

    private suspend fun emitError(message: String) =
        _findPasswordEvent.emit(FindEvent.Error(message = message))

    private fun isEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun timerStart(): CountDownTimer = timer.start()

    fun timerEnd() = timer.cancel()
}
