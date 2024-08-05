package com.ssafy.yoganavi.ui.homeUI.myPage.likeTeacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikeTeacherViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {
    private val _teacherList = MutableStateFlow<List<TeacherData>>(emptyList())
    val teacherList = _teacherList.asStateFlow()

    fun getList() = viewModelScope.launch {
        runCatching { infoRepository.getLikeTeacherList() }
            .onSuccess { _teacherList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }

    fun teacherLikeToggle(teacherId: Int) = viewModelScope.launch {
        runCatching { infoRepository.teacherLikeToggle(teacherId) }
            .onSuccess { getList() }
            .onFailure { it.printStackTrace() }
    }
}
