package com.ssafy.yoganavi.ui.homeUI.myPage.profile

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentProfileBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.MY_PAGE

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(true,MY_PAGE,false)
        initListener()
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
