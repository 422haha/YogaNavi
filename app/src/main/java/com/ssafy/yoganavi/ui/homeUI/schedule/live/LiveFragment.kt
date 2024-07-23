package com.ssafy.yoganavi.ui.homeUI.schedule.live

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.databinding.FragmentLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment

class LiveFragment : BaseFragment<FragmentLiveBinding>(FragmentLiveBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibtnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
