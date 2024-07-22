package com.ssafy.yoganavi.ui.homeUI.myPage.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentProfileBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.core.MainEvent
import com.ssafy.yoganavi.ui.core.MainViewModel
import com.ssafy.yoganavi.ui.utils.MY_PAGE

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        initListener()
    }

    private fun setToolbar() {
        val mainEvent = MainEvent(
            isBottomNavigationVisible = true,
            title = MY_PAGE,
            canGoBack = false,
        )
        activityViewModel.setMainEvent(mainEvent)
    }

    private fun initListener() {
        with(binding) {
            tvLikeTeacher.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_likeTeacherFragment) }
            tvLikeLecture.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_likeLectureFragment) }
            tvMyList.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_myListFragment) }
            tvModify.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_modifyFragment) }
            tvManagementLive.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_managementLiveFragment) }
            tvManagementVideo.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_managementVideoFragment) }
            tvRegisterNotice.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_noticeFragment) }
        }
    }
}
