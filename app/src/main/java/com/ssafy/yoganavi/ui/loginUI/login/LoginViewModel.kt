package com.ssafy.yoganavi.ui.loginUI.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.dataStore.DataStoreRepository
import com.ssafy.yoganavi.data.repository.response.DetailResponse
import com.ssafy.yoganavi.data.repository.user.UserRepository
import com.ssafy.yoganavi.data.source.user.UserRequest
import com.ssafy.yoganavi.ui.utils.HAS_SPACE
import com.ssafy.yoganavi.ui.utils.IS_BLANK
import com.ssafy.yoganavi.ui.utils.IS_NOT_EMAIL
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
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _loginEvent: MutableSharedFlow<LogInEvent> = MutableSharedFlow()
    val loginEvent: SharedFlow<LogInEvent> = _loginEvent.asSharedFlow()

    fun login(email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        if (arrayOf(email, password).isBlank()) {
            emitError(IS_BLANK)
            return@launch
        }

        // TODO 우선 테스트 로그인 할 때에는 로긴 되게 나중에는 수행해야 함!
//        if(!isEmail(email)){
//            emitError(IS_NOT_EMAIL)
//            return@launch
//        }

        if(hasSpace(password)){
            emitError(HAS_SPACE)
            return@launch
        }

        val request = UserRequest(email = email, password = password)
        runCatching { userRepository.logIn(request, dataStoreRepository.getFirebaseToken()) }
            .onSuccess { emitResponse(it) }
            .onFailure { emitError(it.message ?: NO_RESPONSE) }
    }

    private suspend fun emitResponse(response: DetailResponse<Boolean>) = when (response) {
        is DetailResponse.Success -> emitSuccess(response)
        is DetailResponse.Error -> emitError(response.message)
        is DetailResponse.AuthError -> emitError(NO_AUTH)
    }

    private suspend fun emitSuccess(response: DetailResponse.Success<Boolean>) =
        response.data?.let { _loginEvent.emit(LogInEvent.LoginSuccess(it, response.message)) }

    private suspend fun emitError(message: String) =
        _loginEvent.emit(LogInEvent.LoginError(message = message))

    private fun isEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun hasSpace(input: String): Boolean {
        val regex = "\\s".toRegex()
        return regex.containsMatchIn(input)
    }

}
