package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.data.source.ai.KeyPoint
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import com.ssafy.yoganavi.databinding.FragmentLectureDetailBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture.LectureDetailAdapter
import com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture.LectureDetailItem
import com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture.LectureHeader
import com.ssafy.yoganavi.ui.utils.DOWNLOAD
import com.ssafy.yoganavi.ui.utils.DOWNLOAD_VIDEO
import com.ssafy.yoganavi.ui.utils.FAIL_DOWNLOAD
import com.ssafy.yoganavi.ui.utils.INFER
import com.ssafy.yoganavi.ui.utils.LECTURE
import com.ssafy.yoganavi.ui.utils.getYogaDirectory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class LectureDetailFragment : BaseFragment<FragmentLectureDetailBinding>(
    FragmentLectureDetailBinding::inflate
) {
    private val viewModel: LectureDetailViewModel by viewModels()
    private val args by navArgs<LectureDetailFragmentArgs>()
    private val lectureDetailAdapter by lazy {
        LectureDetailAdapter(
            goChapterVideo = ::moveToVideo,
            loadS3ImageSequentially = ::loadS3ImageSequentially,
            loadS3VideoFrame = ::loadS3VideoFrame
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = lectureDetailAdapter
        getLectureDetail()
    }

    override fun onStart() {
        super.onStart()
        setToolbar(
            isBottomNavigationVisible = false,
            title = args.teacher.toTitle(),
            canGoBack = true
        )
    }

    private fun getLectureDetail() = viewModel.getLecture(args.recordedId, ::bindData)

    private suspend fun bindData(data: LectureDetailData) = withContext(Dispatchers.Main) {
        val itemList: MutableList<LectureDetailItem> = mutableListOf()
        val header = LectureHeader(
            recordedId = data.recordedId,
            recordTitle = data.recordTitle,
            recordContent = data.recordContent,
            imageKey = data.imageKey,
            smallImageKey = data.smallImageKey,
        )
        itemList.add(LectureDetailItem.Header(header))

        data.recordedLectureChapters.forEach { chapterItem ->
            itemList.add(LectureDetailItem.Item(chapterItem))
        }

        lectureDetailAdapter.submitList(itemList)
    }

    private fun loadS3ImageSequentially(view: ImageView, smallKey: String, largeKey: String) =
        viewModel.loadS3Image(view, smallKey, largeKey)

    private fun loadS3VideoFrame(view: ImageView, key: String, time: Long, isCircularOn: Boolean) =
        viewModel.loadS3VideoFrame(view, key, time, isCircularOn)

    private fun moveToVideo(start: Int) {
        val keyArray = lectureDetailAdapter.currentList
            .asSequence()
            .drop(start)
            .map { it as LectureDetailItem.Item }
            .map { it.chapterData.recordKey }
            .toList()
            .toTypedArray()

        downloadView()
        val keyAndFileArray = makeFile(keyArray)
        downloadAndInfer(keyAndFileArray)
    }

    private fun makeFile(keyArray: Array<String>): Array<Pair<String, File>> {
        val yogaDir = getYogaDirectory(requireContext())
        if (!yogaDir.exists()) yogaDir.mkdirs()

        val downloadDir = File(yogaDir, DOWNLOAD)
        if (!downloadDir.exists()) downloadDir.mkdirs()

        return keyArray.map { key ->
            val fileName = key.substringAfterLast(delimiter = "/")
            val newFile = File(downloadDir, fileName)
            if (!newFile.exists()) newFile.createNewFile()
            Pair(key, newFile)
        }.toTypedArray()
    }

    private fun downloadAndInfer(keyAndFileArray: Array<Pair<String, File>>) =
        viewLifecycleOwner.lifecycleScope.launch {
            val poseList: Array<List<List<KeyPoint>>> = viewModel.downloadAndInfer(
                keyAndFileArray = keyAndFileArray,
                startInference = ::startInference,
                failToDownload = ::failToDownload
            ).await()

            // TODO pose List도 전달

            val urlArray = keyAndFileArray.map { it.second.absolutePath }.toTypedArray()
            if (poseList.isNotEmpty()) moveToLectureVideoFragment(urlArray)
        }

    private suspend fun moveToLectureVideoFragment(urlArray: Array<String>) =
        withContext(Dispatchers.Main) {
            val directions = LectureDetailFragmentDirections
                .actionLectureDetailFragmentToLectureVideoFragment(urlArray)

            findNavController().navigate(directions)
        }

    private fun downloadView() = with(binding) {
        vBg.apply {
            visibility = View.VISIBLE
            isClickable = true
            isFocusable = true
        }

        tvDownload.apply {
            text = DOWNLOAD_VIDEO
            visibility = View.VISIBLE
        }

        lav.visibility = View.VISIBLE
    }

    private suspend fun startInference() = withContext(Dispatchers.Main) {
        binding.tvDownload.text = INFER
    }

    private suspend fun failToDownload() = withContext(Dispatchers.Main) {
        with(binding) {
            vBg.visibility = View.GONE
            lav.visibility = View.GONE
            tvDownload.visibility = View.GONE
            showSnackBar(FAIL_DOWNLOAD)
        }
    }

    private fun String.toTitle(): String {
        val name = if (length > 7) "${substring(0, 7)}..." else this
        return "${name}님의 $LECTURE"
    }
}
