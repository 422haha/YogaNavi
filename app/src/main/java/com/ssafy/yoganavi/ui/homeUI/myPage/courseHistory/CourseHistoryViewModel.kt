package com.ssafy.yoganavi.ui.homeUI.myPage.courseHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.response.AuthException
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseHistoryViewModel@Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {
    private val _courseHistoryList = MutableStateFlow<List<HomeData>>(emptyList())
    val courseHistoryList = _courseHistoryList.asStateFlow()

    fun getCourseHistoryList(endSession: suspend () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getCourseHistoryList() }
            .onSuccess { _courseHistoryList.emit(it.data.toMutableList()) }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }
}
