package com.ssafy.yoganavi.ui.homeUI.myPage.managementLive

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import com.ssafy.yoganavi.databinding.FragmentManagementLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManagementLiveFragment :
    BaseFragment<FragmentManagementLiveBinding>(FragmentManagementLiveBinding::inflate) {

    private lateinit var liveRecycler: RecyclerView
    private val liveAdapter by lazy { ManagementLiveAdapter(::navigateToLiveFragment) }

    private val viewModel: ManagementLiveViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()

        initRecyclerView()

        initCollect()

        viewModel.getLiveList()
    }

    private fun initListener() {
        with(binding) {
            floatingActionButton.setOnClickListener {
                findNavController().navigate(R.id.action_managementLiveFragment_to_registerLiveFragment)
            }
        }
    }

    private fun initRecyclerView() {
        liveRecycler = binding.rvLiveList

        liveRecycler.apply {
            adapter = liveAdapter
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.liveList.collectLatest {
                liveAdapter.submitList(it)
            }
        }
    }

    private fun navigateToLiveFragment(liveId: Int = -1) {
        val directions = ManagementLiveFragmentDirections
            .actionManagementLiveFragmentToLiveFragment(liveId)

        findNavController().navigate(directions)
    }
}

val dummyList: List<LiveLectureData> = listOf(
    LiveLectureData(
        0,
        liveTitle = "요가 제목",
        liveContent = "요가 컨텐츠",
        maxNum = 1,
        startDate = 1721372190,
        endDate = 1721372190,
        startTime = 36400,
        endTime = 56400,
        regTime = 1721372190,
        teacherId = 0,
        availableDay = "월,목"
    ),
    LiveLectureData(
        0,
        liveTitle = "요가 제목",
        liveContent = "요가 컨텐츠",
        maxNum = 1,
        startDate = 1721372190,
        endDate = 1721372190,
        startTime = 36400,
        endTime = 56400,
        regTime = 1721372190,
        teacherId = 0,
        availableDay = "월,목"
    ),
    LiveLectureData(
        0,
        liveTitle = "요가 제목",
        liveContent = "요가 컨텐츠",
        maxNum = 1,
        startDate = 1721372190,
        endDate = 1721372190,
        startTime = 36400,
        endTime = 56400,
        regTime = 1721372190,
        teacherId = 0,
        availableDay = "월,목"
    ),
    LiveLectureData(
        0,
        liveTitle = "요가 제목",
        liveContent = "요가 컨텐츠",
        maxNum = 1,
        startDate = 1721372190,
        endDate = 1721372190,
        startTime = 36400,
        endTime = 56400,
        regTime = 1721372190,
        teacherId = 0,
        availableDay = "월,목"
    ),
    LiveLectureData(
        0,
        liveTitle = "요가 제목",
        liveContent = "요가 컨텐츠",
        maxNum = 1,
        startDate = 1721372190,
        endDate = 1721372190,
        startTime = 36400,
        endTime = 56400,
        regTime = 1721372190,
        teacherId = 0,
        availableDay = "월,목"
    ),
    LiveLectureData(
        0,
        liveTitle = "요가 제목",
        liveContent = "요가 컨텐츠",
        maxNum = 1,
        startDate = 1721372190,
        endDate = 1721372190,
        startTime = 36400,
        endTime = 56400,
        regTime = 1721372190,
        teacherId = 0,
        availableDay = "월,목"
    ),
    LiveLectureData(
        0,
        liveTitle = "요가 제목",
        liveContent = "요가 컨텐츠",
        maxNum = 1,
        startDate = 1721372190,
        endDate = 1721372190,
        startTime = 36400,
        endTime = 56400,
        regTime = 1721372190,
        teacherId = 0,
        availableDay = "월,목"
    )
)