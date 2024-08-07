package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherDetailData
import com.ssafy.yoganavi.ui.utils.loadS3Image
import com.ssafy.yoganavi.ui.utils.loadS3ImageSequentially
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherDetailViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val s3Client: AmazonS3Client
) : ViewModel() {

    fun getTeacherDetail(
        teacherId: Int,
        bindData: suspend (TeacherDetailData) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getTeacherDetail(teacherId) }
            .onSuccess { it.data?.let { data -> bindData(data) } }
            .onFailure { it.printStackTrace() }
    }

    fun likeLecture(lectureId: Long) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.likeLecture(lectureId) }
            .onFailure { it.printStackTrace() }
    }

    fun loadS3Image(view: ImageView, key: String) = view.loadS3Image(key, s3Client)

    fun loadS3ImageSequentially(
        view: ImageView,
        smallKey: String,
        largeKey: String
    ) = view.loadS3ImageSequentially(smallKey, largeKey, s3Client)
}
