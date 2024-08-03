package com.ssafy.yoganavi.ui.homeUI.myPage.likeLecture

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.data.source.dto.home.EmptyData
import com.ssafy.yoganavi.databinding.FragmentLikeLectureBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo.lecture.LectureAdapter
import com.ssafy.yoganavi.ui.utils.EMPTY_LIKE_LECTURE
import com.ssafy.yoganavi.ui.utils.LIKE_LECTURE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LikeLectureFragment : BaseFragment<FragmentLikeLectureBinding>(
    FragmentLikeLectureBinding::inflate
) {
    private val viewModel: LikeLectureViewModel by viewModels()
    private val lectureAdapter: LectureAdapter by lazy {
        LectureAdapter(
            navigateToLectureDetailFragment = ::navigateToLectureDetailFragment,
            sendLikeLecture = ::sendLikeLecture
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(
            isBottomNavigationVisible = false,
            title = LIKE_LECTURE,
            canGoBack = true
        )

        binding.rvLecture.adapter = lectureAdapter
        initCollect()
        viewModel.getLectureList()
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.lectureList.collectLatest { lectureList ->
                checkEmptyList(lectureList, EmptyData(EMPTY_LIKE_LECTURE))
                lectureAdapter.submitList(lectureList)
            }
        }
    }

    private fun navigateToLectureDetailFragment(recordedId: Long = -1L) {
        val directions = LikeLectureFragmentDirections
            .actionLikeLectureFragmentToLectureDetailFragment(recordedId)

        findNavController().navigate(directions)
    }

    private fun sendLikeLecture(recordedId: Long) = viewModel.setLectureLike(recordedId)
}
