package com.ssafy.yoganavi.ui.homeUI.lecture.lectureVideo

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.annotation.OptIn
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
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

    @OptIn(UnstableApi::class)
    fun inferVideo(
        player: ExoPlayer,
        width: Int,
        height: Int
    ) = viewModelScope.launch(Dispatchers.Default) {
        runCatching {
            if (isVideoInfer) return@launch

            isVideoInfer = true

            val videoTime = withContext(Dispatchers.Main) {
                val position = player.currentPosition
                val duration = player.duration
                val uri = player.currentMediaItem?.localConfiguration?.uri?.toString()

                val fps = uri?.let { getVideoFrameRate() } ?: 30
                val totalFrames = getTotalFrameCount(duration, fps)

                return@withContext VideoTime(position, totalFrames, duration, uri)
            }

            if (videoTime.position == 0L || videoTime.uri.isNullOrBlank()) {
                isVideoInfer = false
                return@launch
            }

            retriever.setDataSource(videoTime.uri)
            val bitmap = getBitmap(videoTime.position, videoTime.totalFrames, videoTime.duration)

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


    private fun getVideoFrameRate(): Int {
        val frameRate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)?.toFloatOrNull()
        retriever.release()
        return frameRate?.toInt() ?: 30
    }

    fun getTotalFrameCount(duration: Long, fps: Int): Int {
        return (duration / 1000 * fps).toInt()
    }

    private fun getBitmap(position: Long, totalFrames: Int, duration: Long): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            retriever.getFrameAtIndex(estimateFrameCount(position, totalFrames, duration))
        } else {
            retriever.getFrameAtTime(position * 1000L)
        }
    }

    private fun estimateFrameCount(position: Long, totalFrames: Int, duration: Long): Int {
        val fraction = position.toDouble() / duration.toDouble()
        return (fraction * totalFrames).toInt()
    }

    override fun onCleared() {
        super.onCleared()
        retriever.release()
    }
}
