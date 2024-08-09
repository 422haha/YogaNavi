package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail

import android.media.MediaMetadataRetriever
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.ssafy.yoganavi.data.repository.ai.PoseRepository
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.ai.KeyPoint
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import com.ssafy.yoganavi.ui.utils.downloadFile
import com.ssafy.yoganavi.ui.utils.loadS3ImageSequentially
import com.ssafy.yoganavi.ui.utils.loadS3VideoFrame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val poseRepository: PoseRepository,
    private val transferUtility: TransferUtility,
    private val s3Client: AmazonS3Client
) : ViewModel() {

    fun getLecture(
        recordId: Long,
        bindData: suspend (LectureDetailData) -> Unit,
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLecture(recordId) }
            .onSuccess { it.data?.let { data -> bindData(data) } }
            .onFailure { it.printStackTrace() }
    }

    fun loadS3Image(view: ImageView, smallKey: String, largeKey: String) =
        view.loadS3ImageSequentially(smallKey, largeKey, s3Client)

    fun loadS3VideoFrame(view: ImageView, key: String, time: Long, isCircularOn: Boolean) =
        view.loadS3VideoFrame(key, time, isCircularOn, s3Client)

    fun downloadAndInfer(
        keyAndFileArray: Array<Pair<String, File>>,
        startInference: suspend () -> Unit,
        failToDownload: suspend () -> Unit
    ): Deferred<Array<List<List<KeyPoint>>>> = viewModelScope.async(Dispatchers.IO) {

        val result: List<Boolean> = keyAndFileArray.map {
            async { downloadFile(transferUtility, it.first, it.second) }
        }.toList().awaitAll()

        if (result.any { false }) {
            failToDownload()
            return@async emptyArray()
        }
        startInference()

        return@async keyAndFileArray
            .map { it.second }
            .map { file -> async { inferVideo(file) } }.awaitAll()
            .toTypedArray()
    }

    private suspend fun inferVideo(file: File): List<List<KeyPoint>> = withContext(Dispatchers.Default){
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val duration = (durationStr?.toLong() ?: 0L) / 100L
        val list = MutableList(duration.toInt()) { emptyList<KeyPoint>() }

        for (i in 0 until list.size) {
            val bitmap = retriever.getFrameAtTime(i * 1000L) ?: continue
            val keyPoints = poseRepository.infer(bitmap, bitmap.width, bitmap.height)
            list[i] = keyPoints.firstOrNull().orEmpty()
            bitmap.recycle()
        }

        return@withContext list
    }

}
