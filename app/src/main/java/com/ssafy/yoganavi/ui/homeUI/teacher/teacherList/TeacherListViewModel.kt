package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.teacher.FilterData
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.ui.utils.POPULAR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherListViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    private val _teacherList = MutableStateFlow<List<TeacherData>>(emptyList())
    val teacherList = _teacherList.asStateFlow()
    private var searchKeyword: String = ""
    private val _sorting = MutableStateFlow(POPULAR)
    val sorting = _sorting.asStateFlow()
    private var isInit: Boolean = true

    fun initCheckGetTeacherList(filter: FilterData) {
        if (isInit) {
            getAllTeacherList()
        } else {
            getTeacherList(filter)
        }
    }

    private fun getTeacherList(filter: FilterData) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getTeacherList(sorting.value, filter, searchKeyword) }
            .onSuccess { _teacherList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }

    private fun getAllTeacherList() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getAllTeacherList(sorting.value, searchKeyword) }
            .onSuccess { _teacherList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }

    fun teacherLikeToggle(
        filter: FilterData,
        teacherId: Int
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.teacherLikeToggle(teacherId) }
            .onSuccess { initCheckGetTeacherList(filter) }
            .onFailure { it.printStackTrace() }
    }

    fun setSearchKeyword(filter: FilterData, newString: String?) {
        searchKeyword = newString ?: searchKeyword
        initCheckGetTeacherList(filter)
    }

    fun getSearchKeyword(): String {
        return searchKeyword
    }

    suspend fun setSorting(newSorting: Int, filter: FilterData) {
        _sorting.emit(newSorting)
        initCheckGetTeacherList(filter)
    }

    fun setIsInit() {
        isInit = true
    }

    fun setIsntInit() {
        isInit = false
    }

    fun getIsInit(): Boolean {
        return isInit
    }
}
