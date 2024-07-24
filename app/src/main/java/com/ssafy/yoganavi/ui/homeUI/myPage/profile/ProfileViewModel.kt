package com.ssafy.yoganavi.ui.homeUI.myPage.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.DataStoreRepository
import com.ssafy.yoganavi.data.source.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    fun getUserInfo(bindUser: (User) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { dataStoreRepository.userFlow.first() }
            .onSuccess { user -> bindUser(user) }
    }
}
