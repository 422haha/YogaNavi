package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.InfoRepository
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.data.source.teacher.FilterData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TeacherListViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {
    private val _teacherList = MutableStateFlow<List<TeacherData>>(emptyList())
    val teacherList = _teacherList.asStateFlow()
    private var searchKeyword: String = ""
    private var sorting: Int = 0
    private var isInit: Boolean = true

    fun initCheckGetTeacherList(filter: FilterData) {
        Timber.d("싸피 init: ${isInit} sorting:${sorting} serach: ${searchKeyword} filter: ${filter}")
        if (isInit) {
            getAllTeacherList()
        } else {
            getTeacherList(filter)
        }
    }

    private fun getTeacherList(filter: FilterData) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getTeacherList(sorting, filter, searchKeyword) }
            .onSuccess { _teacherList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }

    private fun getAllTeacherList() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getAllTeacherList(sorting, searchKeyword) }
            .onSuccess { }
            .onFailure { it.printStackTrace() }
    }

    fun teacherLikeToggle(filter: FilterData, teacherId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { infoRepository.teacherLikeToggle(teacherId) }
                .onSuccess { initCheckGetTeacherList(filter) }
                .onFailure { it.printStackTrace() }
        }

    fun setSearchKeyword(filter: FilterData, newString: String?) {
        searchKeyword = newString ?: ""
        initCheckGetTeacherList(filter)
    }

    fun getSearchKeyword(): String {
        return searchKeyword
    }

    fun setSorting(newSorting: Int = 0, filter: FilterData) {
        sorting = newSorting
        initCheckGetTeacherList(filter)
    }

    fun getSorting(): Int {
        return sorting
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
