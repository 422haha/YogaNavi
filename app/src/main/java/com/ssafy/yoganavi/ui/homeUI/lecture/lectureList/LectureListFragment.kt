package com.ssafy.yoganavi.ui.homeUI.lecture.lectureList

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.databinding.FragmentLectureListBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.lecture.lectureList.lecture.LectureAdapter
import com.ssafy.yoganavi.ui.utils.DATE
import com.ssafy.yoganavi.ui.utils.FAME
import com.ssafy.yoganavi.ui.utils.LECTURE_LIST
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LectureListFragment : BaseFragment<FragmentLectureListBinding>(
    FragmentLectureListBinding::inflate
) {
    private val viewModel: LectureListViewModel by viewModels()
    private val lectureAdapter by lazy {
        LectureAdapter(
            navigateToLectureDetailFragment = ::navigateToLectureDetailFragment,
            sendLikeLecture = ::sendLikeLecture
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(
            isBottomNavigationVisible = true,
            title = LECTURE_LIST,
            canGoBack = false
        )

        binding.rvLecture.adapter = lectureAdapter
        initListener()
        initCollect()
    }

    private fun initListener() = with(binding) {
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                rbPopular.id -> viewModel.updateSortAndKeyword(sort = FAME)
                rbRecent.id -> viewModel.updateSortAndKeyword(sort = DATE)
            }
        }

        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrBlank()) return false

                viewModel.updateSortAndKeyword(keyword = query)
                binding.rvLecture.scrollToPosition(0)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean = true
        })
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.lectureList.collectLatest { pagingData ->
                lectureAdapter.submitData(pagingData)
            }
        }
    }

    private fun navigateToLectureDetailFragment(recordedId: Long = -1L) {
        val directions = LectureListFragmentDirections
            .actionLectureListFragmentToLectureDetailFragment(recordedId)

        findNavController().navigate(directions)
    }

    private fun sendLikeLecture(recordedId: Long) = viewModel.setLectureLike(recordedId)
}
