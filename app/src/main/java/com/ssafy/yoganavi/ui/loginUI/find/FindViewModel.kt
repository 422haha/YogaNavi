package com.ssafy.yoganavi.ui.loginUI.find

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.ApiResponse
import com.ssafy.yoganavi.data.repository.UserRepository
import com.ssafy.yoganavi.data.source.signup.SignUpRequest
import com.ssafy.yoganavi.data.source.signup.SignUpResponse
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
class FindViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _findPasswordEvent: MutableSharedFlow<ApiResponse<SignUpResponse>> =
        MutableSharedFlow()
    private val _checkPasswordEvent: MutableSharedFlow<ApiResponse<SignUpResponse>> =
        MutableSharedFlow()
    private val _registerPasswordEvent: MutableSharedFlow<ApiResponse<SignUpResponse>> =
        MutableSharedFlow()

    val findPasswordEvent: SharedFlow<ApiResponse<SignUpResponse>> =
        _findPasswordEvent.asSharedFlow()
    val checkPasswordEvent: SharedFlow<ApiResponse<SignUpResponse>> =
        _checkPasswordEvent.asSharedFlow()
    val registerPasswordEvent: SharedFlow<ApiResponse<SignUpResponse>> =
        _registerPasswordEvent.asSharedFlow()

    fun findPassword(email: String) = viewModelScope.launch(Dispatchers.IO) {
        if (arrayOf(email).isBlank()) {
            _findPasswordEvent.emit(ApiResponse.Error(IS_BLANK))
            return@launch
        }

        val signUpRequest = SignUpRequest(email = email)

        runCatching { userRepository.findPasswordEmail(signUpRequest) }
            .onSuccess { _findPasswordEvent.emit(it) }
            .onFailure { _findPasswordEvent.emit(ApiResponse.Error(NO_RESPONSE)) }
    }

    fun checkAuthEmail(checkNumber: Int?) = viewModelScope.launch(Dispatchers.IO) {
        checkNumber?.let {
            val signUpRequest = SignUpRequest(authnumber = checkNumber)

            runCatching { userRepository.checkAuthPassword(signUpRequest) }
                .onSuccess { _checkPasswordEvent.emit(it) }
                .onFailure { _checkPasswordEvent.emit(ApiResponse.Error(NO_RESPONSE)) }

        } ?: _checkPasswordEvent.emit(ApiResponse.Error(IS_BLANK))
    }

    fun registerNewPassword(
        email: String,
        password: String,
        passwordAgain: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (arrayOf(email, password).isBlank()) {
            _registerPasswordEvent.emit(ApiResponse.Error(IS_BLANK))
            return@launch
        }

        if (password != passwordAgain) {
            _registerPasswordEvent.emit(ApiResponse.Error(PASSWORD_DIFF))
            return@launch
        }

        val signUpRequest = SignUpRequest(
            email = email,
            password = password
        )

        runCatching { userRepository.registerPassword(signUpRequest) }
            .onSuccess { _registerPasswordEvent.emit(it) }
            .onFailure { _registerPasswordEvent.emit(ApiResponse.Error(NO_RESPONSE)) }
    }
}
