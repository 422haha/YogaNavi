package com.ssafy.yoganavi.ui.homeUI.schedule.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {
    private val _homeList = MutableStateFlow<List<LiveLectureData>>(emptyList())
    val homeList = _homeList.asStateFlow()

    fun getHomeList() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLiveList() }
            .onSuccess { _homeList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }
}
