package com.ssafy.yoganavi.ui.homeUI.schedule.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.databinding.FragmentHomeBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.schedule.home.dialog.EnterDialog
import com.ssafy.yoganavi.ui.utils.HOME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val homeAdapter by lazy { HomeAdapter(::alertLiveDetailDialog) }

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(isBottomNavigationVisible = true,
            title = HOME,
            canGoBack = false)

        binding.rvMyList.adapter = homeAdapter

        initCollect()

        viewModel.getHomeList()
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.homeList.collectLatest {
                checkEmptyList(it)
                homeAdapter.submitList(it)
            }
        }
    }

    private fun alertLiveDetailDialog(id: Int, smallImageUri: String?, imageUri: String?, title: String, content: String) {
        EnterDialog(requireContext(), id, smallImageUri, imageUri, title, content) {
            val directions = HomeFragmentDirections
                .actionHomeFragmentToLiveFragment(it)

            findNavController().navigate(directions)
        }.show()
    }
}
