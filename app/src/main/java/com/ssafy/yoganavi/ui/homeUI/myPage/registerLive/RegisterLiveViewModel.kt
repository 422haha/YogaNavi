package com.ssafy.yoganavi.ui.homeUI.myPage.registerLive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.live.RegisterLiveRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterLiveViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    var liveLectureData = RegisterLiveRequest()

    fun getLive(liveId: Int, onSuccess: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLive(liveId) }
            .onSuccess {
                it.data?.let { data ->
                    liveLectureData = data
                    onSuccess()
                }
            }
            .onFailure { it.printStackTrace() }
    }

    fun createLive() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.createLive(liveLectureData) }
            .onSuccess { Timber.d("생성") }
            .onFailure { it.printStackTrace() }
    }
}
