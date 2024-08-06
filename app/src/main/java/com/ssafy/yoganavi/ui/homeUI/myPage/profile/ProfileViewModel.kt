package com.ssafy.yoganavi.ui.homeUI.myPage.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.dataStore.DataStoreRepository
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.user.UserRepository
import com.ssafy.yoganavi.data.source.dto.mypage.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val infoRepository: InfoRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    fun getProfileData(bindData: suspend (Profile) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { infoRepository.getProfile() }
                .onSuccess { it.data?.let { data -> bindData(data) } }
                .onFailure { it.printStackTrace() }
        }

    fun clearUserData(moveLogin: suspend () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            userRepository.logout()
        }.onSuccess {
            dataStoreRepository.clearToken()
        }.onSuccess {
            moveLogin()
        }
    }

    fun quitUser(showDialog: suspend (String) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            userRepository.quit()
        }.onSuccess { data ->
            dataStoreRepository.clearToken()
            showDialog(data.message)
        }
    }

    suspend fun checkPassword(
        password: String,
        getBoolean: suspend (Boolean?) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            userRepository.checkPassword(hashMapOf("password" to password))
        }.onSuccess { getBoolean(it.data) }
            .onFailure { it.printStackTrace() }
    }
}
