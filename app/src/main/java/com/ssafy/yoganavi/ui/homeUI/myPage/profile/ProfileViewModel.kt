package com.ssafy.yoganavi.ui.homeUI.myPage.profile

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.data.repository.dataStore.DataStoreRepository
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.user.UserRepository
import com.ssafy.yoganavi.data.source.dto.mypage.Profile
import com.ssafy.yoganavi.ui.utils.loadS3Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val infoRepository: InfoRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val s3Client: AmazonS3Client
) : ViewModel() {

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile = _profile.asStateFlow()

    fun getProfileData(): StateFlow<Profile?> {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { infoRepository.getProfile() }
                .onSuccess { _profile.value = it.data }
                .onFailure { it.printStackTrace() }
        }
        return _profile
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

    suspend fun checkPassword(password: String): Deferred<Boolean> =
        viewModelScope.async(Dispatchers.IO) {
            runCatching { userRepository.checkPassword(password) }.fold(
                onSuccess = { result -> return@async result.data == true },
                onFailure = { return@async false }
            )
        }

    fun loadS3Image(view: ImageView, key: String) = view.loadS3Image(key, s3Client)
}
