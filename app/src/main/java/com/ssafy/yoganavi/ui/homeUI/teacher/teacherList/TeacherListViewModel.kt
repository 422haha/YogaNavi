package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherListViewModel @Inject constructor(
    val infoRepository: InfoRepository
) : ViewModel() {
    private val _teacherList = MutableStateFlow<List<TeacherData>>(emptyList())
    val teacherList = _teacherList.asStateFlow()

    fun getTeacherList() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getTeacherList() }
            .onSuccess { _teacherList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }
}
