package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherDetailData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherDetailViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {
    fun getTeacherDetail(
        teacherId: Int,
        bindData: suspend (TeacherDetailData) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getTeacherDetail(teacherId) }
            .onSuccess { it.data?.let { data -> bindData(data) } }
            .onFailure { it.printStackTrace() }
    }
}
