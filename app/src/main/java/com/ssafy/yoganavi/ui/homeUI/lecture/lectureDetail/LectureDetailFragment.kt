package com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.data.source.dto.lecture.LectureDetailData
import com.ssafy.yoganavi.databinding.FragmentLectureDetailBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture.LectureDetailAdapter
import com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture.LectureDetailItem
import com.ssafy.yoganavi.ui.homeUI.lecture.lectureDetail.lecture.LectureHeader
import com.ssafy.yoganavi.ui.utils.LECTURE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LectureDetailFragment : BaseFragment<FragmentLectureDetailBinding>(
    FragmentLectureDetailBinding::inflate
) {
    private val viewModel: LectureDetailViewModel by viewModels()
    private val args by navArgs<LectureDetailFragmentArgs>()
    private val lectureDetailAdapter by lazy { LectureDetailAdapter(::moveToVideo) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(
            isBottomNavigationVisible = false,
            title = args.teacher.toTitle(),
            canGoBack = true
        )

        binding.recyclerView.adapter = lectureDetailAdapter
        getLectureDetail()
    }

    private fun getLectureDetail() = viewModel.getLecture(args.recordedId, ::bindData)

    private suspend fun bindData(data: LectureDetailData) = withContext(Dispatchers.Main) {
        val itemList: MutableList<LectureDetailItem> = mutableListOf()
        val header = LectureHeader(
            recordedId = data.recordedId,
            recordTitle = data.recordTitle,
            recordContent = data.recordContent,
            recordThumbnail = data.recordThumbnail,
            recordThumbnailSmall = data.recordThumbnailSmall,
            nickname = data.nickname
        )
        itemList.add(LectureDetailItem.Header(header))

        data.recordedLectureChapters.forEach { chapterItem ->
            itemList.add(LectureDetailItem.Item(chapterItem))
        }

        lectureDetailAdapter.submitList(itemList)
    }

    private fun moveToVideo(start: Int) {
        val uriList = lectureDetailAdapter.currentList
            .asSequence()
            .drop(start)
            .map { it as LectureDetailItem.Item }
            .map { it.chapterData.recordVideo }
            .toList()
            .toTypedArray()

        val directions = LectureDetailFragmentDirections
            .actionLectureDetailFragmentToLectureVideoFragment(uriList = uriList)

        findNavController().navigate(directions)
    }

    private fun String.toTitle(): String {
        val name = if (length > 10) substring(0, 10) else this
        return "${name}님의 $LECTURE"
    }
}
