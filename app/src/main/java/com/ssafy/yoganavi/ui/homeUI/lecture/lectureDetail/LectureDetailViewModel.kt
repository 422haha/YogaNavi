package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import com.ssafy.yoganavi.ui.utils.keyToUrl
import com.ssafy.yoganavi.ui.utils.loadS3ImageSequentially
import com.ssafy.yoganavi.ui.utils.loadS3VideoFrame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val s3Client: AmazonS3Client
) : ViewModel() {

    fun getLecture(
        recordId: Long,
        bindData: suspend (LectureDetailData) -> Unit,
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLecture(recordId) }
            .onSuccess { it.data?.let { data -> bindData(data) } }
            .onFailure { it.printStackTrace() }
    }

    fun keyToUri(key: String): String = key.keyToUrl(s3Client)

    fun loadS3Image(view: ImageView, smallKey: String, largeKey: String) =
        view.loadS3ImageSequentially(smallKey, largeKey, s3Client)

    fun loadS3VideoFrame(view: ImageView, key: String, time: Long, isCircularOn: Boolean) =
        view.loadS3VideoFrame(key, time, isCircularOn, s3Client)
}
