package com.ssafy.yoganavi.ui.homeUI.lecture.lectureVideo

import android.media.MediaMetadataRetriever
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.ssafy.yoganavi.data.repository.ai.PoseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LectureVideoViewModel @Inject constructor(
    private val poseRepository: PoseRepository
) : ViewModel() {

    private val retriever = MediaMetadataRetriever()
    private var isVideoInfer: Boolean = false

    private val _userKeyPoints: MutableStateFlow<List<FloatArray>> =
        MutableStateFlow(emptyList())
    val userKeyPoints: StateFlow<List<FloatArray>> = _userKeyPoints.asStateFlow()

    private val _teacherKeyPoints: MutableStateFlow<List<FloatArray>> =
        MutableStateFlow(emptyList())
    val teacherKeyPoints: StateFlow<List<FloatArray>> = _teacherKeyPoints.asStateFlow()

    fun inferImage(
        image: ImageProxy,
        width: Int,
        height: Int
    ) = viewModelScope.launch(Dispatchers.Default) {
        val result: List<FloatArray> = poseRepository.infer(image, width, height)
        _userKeyPoints.emit(result)
        image.close()
    }

    fun inferVideo(
        player: ExoPlayer,
        width: Int,
        height: Int
    ) = viewModelScope.launch(Dispatchers.Default) {
        if (isVideoInfer) return@launch

        isVideoInfer = true
        val (position, uri) = withContext(Dispatchers.Main) {
            val position = player.currentPosition
            val uri = player.currentMediaItem?.localConfiguration?.uri?.toString()
            return@withContext Pair(position, uri)
        }

        if (position == 0L || uri.isNullOrBlank()) {
            isVideoInfer = false
            return@launch
        }

        retriever.setDataSource(uri)
        val bitmap = retriever.getFrameAtTime(
            position * 1000,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
        )

        bitmap?.let {
            val result = poseRepository.infer(bitmap, bitmap.width, bitmap.height)
            val ratio = height / bitmap.height.toFloat()
            val diffX = (width - bitmap.width * ratio) / 2f
            result.forEach { points ->
                for (idx in points.indices step 3) {
                    if (points[idx] == 0f || points[idx + 1] == 0f) continue
                    points[idx] = points[idx] * ratio + diffX
                    points[idx + 1] = points[idx + 1] * ratio
                }
            }
            bitmap.recycle()
            _teacherKeyPoints.emit(result)
        }

        isVideoInfer = false
    }

    override fun onCleared() {
        super.onCleared()
        retriever.release()
    }
}
