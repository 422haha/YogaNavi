package com.ssafy.yoganavi.ui.homeUI.myPage.managementLive

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentManagementLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment

class ManagementLiveFragment :
    BaseFragment<FragmentManagementLiveBinding>(FragmentManagementLiveBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()

        binding.
    }

    private fun initListener() {
        with(binding) {
            floatingActionButton.setOnClickListener {
                findNavController().navigate(R.id.action_managementLiveFragment_to_registerLiveFragment)
            }
        }
    }
}
