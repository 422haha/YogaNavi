package com.ssafy.yoganavi.ui.homeUI.lecture.lectureVideo

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.ai.PoseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureVideoViewModel @Inject constructor(
    private val poseRepository: PoseRepository
) : ViewModel() {

    private val _keyPoints: MutableStateFlow<List<FloatArray>> = MutableStateFlow(emptyList())
    val keyPoints: StateFlow<List<FloatArray>> = _keyPoints.asStateFlow()

    fun inferImage(
        image: ImageProxy,
        width: Int,
        height: Int
    ) = viewModelScope.launch(Dispatchers.Default) {
        val result: List<FloatArray> = poseRepository.infer(image, width, height)
        _keyPoints.emit(result)
        image.close()

        // TODO 비디오 추론 및 비교
    }

}
