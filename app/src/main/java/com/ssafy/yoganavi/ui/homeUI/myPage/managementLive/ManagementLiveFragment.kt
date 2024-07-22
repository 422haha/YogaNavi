package com.ssafy.yoganavi.ui.homeUI.myPage.managementLive

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentManagementLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.core.MainEvent
import com.ssafy.yoganavi.ui.core.MainViewModel
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_LIVE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManagementLiveFragment :
    BaseFragment<FragmentManagementLiveBinding>(FragmentManagementLiveBinding::inflate) {

    private val liveAdapter by lazy { ManagementLiveAdapter(::navigateToLiveFragment) }

    private val activityViewModel: MainViewModel by activityViewModels()

    private val viewModel: ManagementLiveViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()

        initListener()

        binding.rvLiveList.adapter = liveAdapter

        initCollect()

        viewModel.getLiveList()
    }

    private fun setToolbar() {
        val mainEvent = MainEvent(
            isBottomNavigationVisible = false,
            title = MANAGEMENT_LIVE,
            canGoBack = true,
        )
        activityViewModel.setMainEvent(mainEvent)
    }

    private fun initListener() {
        with(binding) {
            floatingActionButton.setOnClickListener {
                findNavController().navigate(R.id.action_managementLiveFragment_to_registerLiveFragment)
            }
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