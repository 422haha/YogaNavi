package com.ssafy.yoganavi.ui.homeUI.teacher.teacherList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.response.AuthException
import com.ssafy.yoganavi.data.source.dto.teacher.TeacherData
import com.ssafy.yoganavi.data.source.teacher.FilterData
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
    private val _sorting = MutableStateFlow(0)
    val sorting = _sorting.asStateFlow()
    private var isInit: Boolean = true

    private fun initCheckGetTeacherList(filter: FilterData, endSession: suspend () -> Unit) {
        if (isInit) {
            getAllTeacherList(endSession)
        } else {
            getTeacherList(filter, endSession)
        }
    }

    private fun getTeacherList(
        filter: FilterData,
        endSession: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getTeacherList(sorting.value, filter, searchKeyword) }
            .onSuccess { _teacherList.emit(it.data.toMutableList()) }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }

    private fun getAllTeacherList(
        endSession: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getAllTeacherList(sorting.value, searchKeyword) }
            .onSuccess { _teacherList.emit(it.data.toMutableList()) }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }

    fun teacherLikeToggle(
        filter: FilterData,
        teacherId: Int,
        endSession: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.teacherLikeToggle(teacherId) }
            .onSuccess { initCheckGetTeacherList(filter, endSession) }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }

    fun setSearchKeyword(filter: FilterData, newString: String?, endSession: suspend () -> Unit) {
        searchKeyword = newString ?: ""
        initCheckGetTeacherList(filter, endSession)
    }

    fun getSearchKeyword(): String {
        return searchKeyword
    }

    suspend fun setSorting(newSorting: Int, filter: FilterData, endSession: suspend () -> Unit) {
        _sorting.emit(newSorting)
        initCheckGetTeacherList(filter, endSession)
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
