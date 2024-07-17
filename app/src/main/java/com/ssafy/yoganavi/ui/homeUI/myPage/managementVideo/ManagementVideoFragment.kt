package com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentManagementVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment

class ManagementVideoFragment :
    BaseFragment<FragmentManagementVideoBinding>(FragmentManagementVideoBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener() {
        with(binding) {
            fabEdit.setOnClickListener { findNavController().navigate(R.id.action_managementVideoFragment_to_registerVideoFragment) }
        }
    }
}
