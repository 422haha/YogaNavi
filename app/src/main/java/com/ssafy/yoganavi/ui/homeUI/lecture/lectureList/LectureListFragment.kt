package com.ssafy.yoganavi.ui.homeUI.lecture.lectureList

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.data.source.dto.home.EmptyData
import com.ssafy.yoganavi.databinding.FragmentLectureListBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.lecture.lectureList.lecture.LectureAdapter
import com.ssafy.yoganavi.ui.utils.ANY_CHECK_BOX
import com.ssafy.yoganavi.ui.utils.DATE
import com.ssafy.yoganavi.ui.utils.EMPTY_LECTURE
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

        initAdapter()
        initListener()
        initCollect()
    }

    private fun initAdapter() = with(binding.rvLecture) {
        adapter = lectureAdapter.apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    scrollToPosition(0)
                    super.onItemRangeChanged(positionStart, itemCount)
                }
            })

            addLoadStateListener { loadState ->
                val isListEmpty = loadState.refresh is LoadState.NotLoading &&
                        this.itemCount == 0 && loadState.append.endOfPaginationReached

                if (isListEmpty) setEmptyView(true, EmptyData(EMPTY_LECTURE))
                else setEmptyView(false, EmptyData())
            }
        }
    }

    private fun initListener() = with(binding) {
        cbTitle.setOnClickListener {
            viewModel.updateSortAndKeyword(searchInTitle = cbTitle.isChecked)
        }

        cbContent.setOnClickListener {
            viewModel.updateSortAndKeyword(searchInContent = cbContent.isChecked)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                rbPopular.id -> viewModel.updateSortAndKeyword(sort = FAME)
                rbRecent.id -> viewModel.updateSortAndKeyword(sort = DATE)
            }
        }

        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!anyCheck() && !query.isNullOrBlank()) {
                    showSnackBar(ANY_CHECK_BOX)
                    return false
                }

                if (query.isNullOrBlank()) viewModel.updateSortAndKeyword(keyword = null)
                else viewModel.updateSortAndKeyword(keyword = query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) onQueryTextSubmit("")
                return false
            }
        })
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.lectureList.collectLatest { pagingData ->
                lectureAdapter.submitData(pagingData)
            }
        }
    }

    private fun anyCheck(): Boolean = with(binding) {
        return@with cbTitle.isChecked || cbContent.isChecked
    }

    private fun navigateToLectureDetailFragment(recordedId: Long = -1L) {
        val directions = LectureListFragmentDirections
            .actionLectureListFragmentToLectureDetailFragment(recordedId)

        findNavController().navigate(directions)
    }

    private fun sendLikeLecture(recordedId: Long) =
        viewModel.setLectureLike(recordedId)
}
