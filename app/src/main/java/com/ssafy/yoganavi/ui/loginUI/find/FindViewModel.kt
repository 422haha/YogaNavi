package com.ssafy.yoganavi.ui.loginUI.find

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
class FindViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _findPasswordEvent: MutableSharedFlow<FindEvent<Unit>> = MutableSharedFlow()
    val findPasswordEvent: SharedFlow<FindEvent<Unit>> = _findPasswordEvent.asSharedFlow()

    fun sendEmail(email: String) = viewModelScope.launch(Dispatchers.IO) {
        if (arrayOf(email).isBlank()) {
            emitError(IS_BLANK)
            return@launch
        }

        val signUpRequest = SignUpRequest(email = email)
        runCatching { userRepository.findPasswordEmail(signUpRequest) }
            .onSuccess { emitResponse(it, FindEvent.SendEmailSuccess::class.java) }
            .onFailure { emitError(NO_RESPONSE) }
    }

    fun checkAuthEmail(checkNumber: Int?) = viewModelScope.launch(Dispatchers.IO) {
        checkNumber?.let {
            val signUpRequest = SignUpRequest(authnumber = checkNumber)
            runCatching { userRepository.checkAuthPassword(signUpRequest) }
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

            if (password != passwordAgain) {
                emitError(PASSWORD_DIFF)
                return@launch
            }

            val signUpRequest = SignUpRequest(
                email = email,
                password = password
            )

            runCatching { userRepository.registerPassword(signUpRequest) }
                .onSuccess { emitResponse(it, FindEvent.RegisterPasswordSuccess::class.java) }
                .onFailure { emitError(NO_RESPONSE) }
        }

    private suspend fun emitResponse(response: ApiResponse<Unit>, type: Class<out FindEvent<*>>) =
        when (response) {
            is ApiResponse.Success -> emitSuccess(response, type)
            is ApiResponse.Error -> emitError(response.message)
        }

    private suspend fun emitSuccess(response: ApiResponse<Unit>, type: Class<out FindEvent<*>>) =
        when (type) {
            FindEvent.SendEmailSuccess::class.java -> {
                _findPasswordEvent.emit(
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
}
