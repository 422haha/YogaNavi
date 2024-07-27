package com.ssafy.yoganavi.ui.homeUI.schedule.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentHomeBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.managementLive.ManagementLiveAdapter
import com.ssafy.yoganavi.ui.homeUI.myPage.managementLive.ManagementLiveViewModel
import com.ssafy.yoganavi.ui.utils.HOME
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_LIVE
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
                homeAdapter.submitList(it)
            }
        }
    }

    private fun alertLiveDetailDialog(id: Int, imageUri: String, title: String, content: String) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_enter, null)
        builder.setView(dialogView)

        // Big size Image
//        Glide.with(binding.root)
//            .load(imageUri)
//            .into() // dialog image
    }
}
