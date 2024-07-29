package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture

import android.media.MediaMetadataRetriever
import android.widget.ImageView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.data.source.dto.lecture.VideoChapterData
import com.ssafy.yoganavi.databinding.ListItemLectureBinding
import com.ssafy.yoganavi.ui.utils.msToDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LectureDetailItemViewHolder(
    private val binding: ListItemLectureBinding,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope,
    private val goChapterVideo: (String) -> Unit
) : ViewHolder(binding.root) {

    fun bind(data: VideoChapterData) = with(binding) {
        tvVideoTitle.text = data.chapterTitle
        tvVideoContent.text = data.chapterDescription

        val uri = when {
            data.recordPath.isNotBlank() -> data.recordPath
            data.recordVideo.isNotBlank() -> data.recordVideo
            else -> ""
        }
        if (uri.isNotBlank()) ivLecture.setVideoThumbnail(uri)

        itemView.setOnClickListener { goChapterVideo(uri) }
    }

    private fun ImageView.setVideoThumbnail(url: String) =
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            val bitmap = runCatching {
                retriever.setDataSource(url, HashMap())
                retriever.getFrameAtTime(0L)
            }.getOrNull()

            val duration = runCatching {
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
            }.getOrNull()

            withContext(Dispatchers.Main) {
                bitmap?.let { setImageBitmap(it) }
                duration?.let { binding.tvVideoLength.text = it.msToDuration() }
            }

            retriever.release()
        }
}