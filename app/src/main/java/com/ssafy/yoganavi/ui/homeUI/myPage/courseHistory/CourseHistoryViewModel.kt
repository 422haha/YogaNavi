package com.ssafy.yoganavi.ui.homeUI.myPage.courseHistory

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import com.ssafy.yoganavi.ui.utils.loadS3Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseHistoryViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val s3Client: AmazonS3Client
) : ViewModel() {
    private val _courseHistoryList = MutableStateFlow<List<HomeData>>(emptyList())
    val courseHistoryList = _courseHistoryList.asStateFlow()

    fun getCourseHistoryList() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getCourseHistoryList() }
            .onSuccess { _courseHistoryList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }

    fun loadS3Image(view: ImageView, key: String) = view.loadS3Image(key, s3Client)
}
