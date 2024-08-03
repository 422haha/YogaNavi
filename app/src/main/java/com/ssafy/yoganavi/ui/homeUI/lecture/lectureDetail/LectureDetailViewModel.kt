package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    fun getLecture(
        recordId: Long,
        bindData: suspend (LectureDetailData) -> Unit,
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLecture(recordId) }
            .onSuccess { it.data?.let { data -> bindData(data) } }
            .onFailure { it.printStackTrace() }
    }

}
