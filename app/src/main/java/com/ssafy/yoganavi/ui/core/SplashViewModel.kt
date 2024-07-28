package com.ssafy.yoganavi.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.DataStoreRepository
import com.ssafy.yoganavi.data.repository.UserRepository
import com.ssafy.yoganavi.data.repository.response.ListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var isSuccess = false

    fun autoLogin() = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            val token = dataStoreRepository.accessToken.firstOrNull() ?: return@launch
            if (token.isBlank()) return@launch

            val response = userRepository.isServerOn()
            if (response !is ListResponse.Success) return@launch
        }.onSuccess { isSuccess = true }
    }

}