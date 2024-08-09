package com.ssafy.yoganavi.ui.homeUI.lecture.lectureVideo

import android.media.MediaMetadataRetriever
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.ssafy.yoganavi.data.repository.ai.PoseRepository
import com.ssafy.yoganavi.data.source.ai.KeyPoint
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
    private var averageInferTime: Float = 0f

    private val _userKeyPoints: MutableStateFlow<List<KeyPoint>> =
        MutableStateFlow(emptyList())
    val userKeyPoints: StateFlow<List<KeyPoint>> = _userKeyPoints.asStateFlow()

    private val _teacherKeyPoints: MutableStateFlow<List<KeyPoint>> =
        MutableStateFlow(emptyList())
    val teacherKeyPoints: StateFlow<List<KeyPoint>> = _teacherKeyPoints.asStateFlow()

    fun inferImage(
        image: ImageProxy,
        width: Int,
        height: Int
    ) = viewModelScope.launch(Dispatchers.Default) {
        val result: List<List<KeyPoint>> = poseRepository.infer(image, width, height)
        _userKeyPoints.emit(result.firstOrNull().orEmpty())
        image.close()
    }

    fun inferVideo(
        player: ExoPlayer,
        width: Int,
        height: Int
    ) = viewModelScope.launch(Dispatchers.Default) {
        runCatching {
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

            val startTime = System.currentTimeMillis()
            retriever.setDataSource(uri)
            val bitmap = retriever.getFrameAtTime(
                (position + averageInferTime.toLong()) * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )
            val endTime = System.currentTimeMillis()
            averageInferTime = ((endTime - startTime) + averageInferTime) / 2f

            bitmap?.let {
                val results = poseRepository.infer(bitmap, bitmap.width, bitmap.height)
                val ratio = height / bitmap.height.toFloat()
                val diffX = (width - bitmap.width * ratio) / 2f
                val result = results.firstOrNull().orEmpty()
                val keyPoints = mutableListOf<KeyPoint>()
                result.forEachIndexed { idx, keyPoint ->
                    val x = if (keyPoint.x == 0f) 0f else keyPoint.x * ratio + diffX
                    val y = if (keyPoint.y == 0f) 0f else keyPoint.y * ratio
                    keyPoints.add(KeyPoint(idx, x, y, keyPoint.confidence))
                }
                bitmap.recycle()
                _teacherKeyPoints.emit(keyPoints)
            }

            isVideoInfer = false
        }.onFailure {
            isVideoInfer = false
        }
    }


    override fun onCleared() {
        super.onCleared()
        retriever.release()
    }
}
