package com.ssafy.yoganavi.ui.homeUI.schedule.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.data.source.dto.home.EmptyData
import com.ssafy.yoganavi.databinding.FragmentHomeBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.schedule.home.dialog.EnterDialog
import com.ssafy.yoganavi.ui.utils.EMPTY_LIVE
import com.ssafy.yoganavi.ui.utils.HOME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val homeAdapter by lazy { HomeAdapter(::alertLiveDetailDialog, ::loadS3Image) }

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        initCollect()
    }

    override fun onStart() {
        super.onStart()
        setToolbar(
            isBottomNavigationVisible = true,
            title = HOME,
            canGoBack = false
        )
    }

    private fun initAdapter() = with(binding.rvMyList) {
        binding.rvMyList.adapter = homeAdapter

        binding.srl.setOnRefreshListener {
            homeAdapter.refresh()

            binding.srl.isRefreshing = false
        }

        adapter = homeAdapter.apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    scrollToPosition(positionStart)
                    super.onItemRangeChanged(positionStart, itemCount)
                }
            })

            addLoadStateListener { loadState ->
                val isListEmpty = loadState.refresh is LoadState.NotLoading &&
                        this.itemCount == 0 && loadState.append.endOfPaginationReached

                if (isListEmpty) setEmptyView(EmptyData(true, EMPTY_LIVE))
                else setEmptyView(EmptyData(false))
            }
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.homeList2.collectLatest { pagingData ->
                homeAdapter.submitData(pagingData)
            }
        }
    }

    private fun alertLiveDetailDialog(
        id: Int,
        smallImageUri: String?,
        imageUri: String?,
        title: String,
        content: String,
        isTeacher: Boolean,
    ) {
        EnterDialog(
            context = requireContext(),
            smallImageUri = smallImageUri,
            imageUri = imageUri,
            title = title,
            content = content,
            okCallback = { moveToLiveFragment(id, isTeacher) },
            loadS3ImageSequentially = { imageView, smallUri, largeUri ->
                loadS3ImageSequentially(imageView, smallUri, largeUri)
            }
        ).show()
    }

    private fun moveToLiveFragment(id: Int, isTeacher: Boolean) {
        val directions = HomeFragmentDirections.actionHomeFragmentToLiveFragment(id, isTeacher)
        findNavController().navigate(directions)
    }

    private fun loadS3Image(view: ImageView, key: String) = viewModel.loadS3Image(
        view, key
    )

    private fun loadS3ImageSequentially(view: ImageView, smallKey: String, largeKey: String) =
        viewModel.loadS3ImageSequentially(view, smallKey, largeKey)
}
