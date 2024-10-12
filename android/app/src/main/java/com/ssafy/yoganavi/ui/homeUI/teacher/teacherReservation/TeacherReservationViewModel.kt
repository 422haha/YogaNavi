package com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.s3.AmazonS3Client
import com.google.android.material.imageview.ShapeableImageView
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData
import com.ssafy.yoganavi.data.source.dto.teacher.LiveReserveRequest
import com.ssafy.yoganavi.ui.utils.SUCCESS
import com.ssafy.yoganavi.ui.utils.loadS3Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherReservationViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val s3Client: AmazonS3Client
) : ViewModel() {
    private val _availableList = MutableStateFlow<List<LiveLectureData>>(emptyList())
    val availableList = _availableList.asStateFlow()

    fun getAvailableClass(teacherId: Int, method: Int) = viewModelScope.launch {
        runCatching { infoRepository.getAvailableClass(teacherId, method) }
            .onSuccess { _availableList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }

    fun registerLive(
        liveId: Int,
        startDate: Long?,
        endDate: Long?,
        navigateToSchedule: () -> (Unit),
        showSnackBar: (String) -> (Unit)
    ) = viewModelScope.launch {
        runCatching {
            infoRepository.registerLive(LiveReserveRequest(liveId, startDate, endDate))
        }.onSuccess {
            if (it.message == SUCCESS) navigateToSchedule()
            else showSnackBar(it.message)
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun loadS3Image(view: ShapeableImageView, key: String) = view.loadS3Image(key, s3Client)
}
