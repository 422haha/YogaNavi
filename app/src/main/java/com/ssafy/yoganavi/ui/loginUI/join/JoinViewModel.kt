package com.ssafy.yoganavi.ui.loginUI.join

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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registerEmailEvent: MutableSharedFlow<ApiResponse<SignUpResponse>> =
        MutableSharedFlow()
    private val _checkEmailEvent: MutableSharedFlow<ApiResponse<SignUpResponse>> =
        MutableSharedFlow()
    private val _signUpEvent: MutableSharedFlow<ApiResponse<SignUpResponse>> =
        MutableSharedFlow()

    val registerEmailEvent: SharedFlow<ApiResponse<SignUpResponse>> =
        _registerEmailEvent.asSharedFlow()
    val checkEmailEvent: SharedFlow<ApiResponse<SignUpResponse>> =
        _checkEmailEvent.asSharedFlow()
    val signUpEvent: SharedFlow<ApiResponse<SignUpResponse>> =
        _signUpEvent.asSharedFlow()

    fun registerEmail(email: String) = viewModelScope.launch {
        if (arrayOf(email).isBlank()) {
            _registerEmailEvent.emit(ApiResponse.Error(IS_BLANK))
            return@launch
        }

        val signUpRequest = SignUpRequest(email = email)

        runCatching { userRepository.registerEmail(signUpRequest) }
            .onSuccess { _registerEmailEvent.emit(it) }
            .onFailure { _registerEmailEvent.emit(ApiResponse.Error(NO_RESPONSE)) }
    }

    fun checkAuthEmail(checkNumber: Int?) = viewModelScope.launch {
        checkNumber?.let {
            val signUpRequest = SignUpRequest(authnumber = checkNumber)

            runCatching { userRepository.checkAuthEmail(signUpRequest) }
                .onSuccess { _checkEmailEvent.emit(it) }
                .onFailure { _checkEmailEvent.emit(ApiResponse.Error(NO_RESPONSE)) }
        }
    }

    fun signUp(
        email: String,
        password: String,
        passwordAgain: String,
        nickname: String,
        isTeacher: Boolean
    ) = viewModelScope.launch {
        if (arrayOf(email, password, nickname).isBlank()) {
            _signUpEvent.emit(ApiResponse.Error(IS_BLANK))
            return@launch
        }

        if (password != passwordAgain) {
            _signUpEvent.emit(ApiResponse.Error(PASSWORD_DIFF))
            return@launch
        }

        val signUpRequest = SignUpRequest(
            email = email,
            password = password,
            nickname = nickname,
            teacher = isTeacher
        )

        runCatching { userRepository.signUp(signUpRequest) }
            .onSuccess { _signUpEvent.emit(it) }
            .onFailure { _signUpEvent.emit(ApiResponse.Error(NO_RESPONSE)) }
    }
}
