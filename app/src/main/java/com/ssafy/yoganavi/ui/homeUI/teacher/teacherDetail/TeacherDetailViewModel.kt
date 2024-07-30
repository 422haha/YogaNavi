package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherDetailViewModel @Inject constructor(
    val infoRepository: InfoRepository
) : ViewModel() {
    fun getTeacherDetail(
        teacherId: Int,
        bindData: suspend (TeacherData) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getTeacherDetail(teacherId) }
            .onSuccess { it.data?.let { data -> bindData(data) } }
            .onFailure { it.printStackTrace() }
    }
}
