package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail

import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.response.AuthException
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    fun getLecture(
        recordId: Long,
        bindData: suspend (LectureDetailData) -> Unit,
        endSession: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLecture(recordId) }
            .onSuccess { it.data?.let { data -> bindData(data) } }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }

    fun getVideoInfo(uri: String) = viewModelScope.async(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        val bitmap = runCatching {
            retriever.setDataSource(uri, HashMap())
            retriever.getFrameAtTime(0L)
        }.getOrNull()

        val duration = runCatching {
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
        }.getOrNull()

        retriever.release()
        return@async Pair(bitmap, duration)
    }

}
