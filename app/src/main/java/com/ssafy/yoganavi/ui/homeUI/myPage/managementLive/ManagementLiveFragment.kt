package com.ssafy.yoganavi.ui.homeUI.myPage.managementLive

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentManagementLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.CREATE
import com.ssafy.yoganavi.ui.utils.EMPTY_MY_LIVE
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_LIVE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManagementLiveFragment :
    BaseFragment<FragmentManagementLiveBinding>(FragmentManagementLiveBinding::inflate) {

    private val liveAdapter by lazy {
        ManagementLiveAdapter(
            ::navigateToLiveFragment,
            ::navigateToRegisterFragment,
            ::deleteLive,
            ::showSnackBar
        )
    }

    private val viewModel: ManagementLiveViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()

        binding.rvLiveList.adapter = liveAdapter

        initCollect()

        viewModel.getLiveList()
    }

    override fun onStart() {
        super.onStart()
        setToolbar(
            isBottomNavigationVisible = false,
            title = MANAGEMENT_LIVE,
            canGoBack = true,
        )
    }

    private fun initListener() {
        with(binding) {
            floatingActionButton.setOnClickListener {
                navigateToRegisterFragment(state = CREATE, liveId = -1)
            }
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.liveList.collectLatest {
                checkEmptyList(it, EMPTY_MY_LIVE)
                liveAdapter.submitList(it)
            }
        }
    }

    private fun navigateToLiveFragment(liveId: Int, isTeacher: Boolean) {
        val directions = ManagementLiveFragmentDirections
            .actionManagementLiveFragmentToLiveFragment(liveId, isTeacher)

        findNavController().navigate(directions)
    }

    private fun navigateToRegisterFragment(state: String, liveId: Int = -1) {
        val directions = ManagementLiveFragmentDirections
            .actionManagementLiveFragmentToRegisterLiveFragment(state = state, liveId = liveId)

        findNavController().navigate(directions)
    }

    private fun deleteLive(liveId: Int = -1) {
        if (liveId != -1) {
            showDeleteDialog(liveId)
        }
    }

    private fun showDeleteDialog(liveId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("화상강의 삭제")
            .setMessage("정말로 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                viewModel.deleteLive(liveId, ::removeLive)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun removeLive() {
        viewModel.getLiveList()
        showSnackBar(getString(R.string.live_delete_msg))
    }
}
