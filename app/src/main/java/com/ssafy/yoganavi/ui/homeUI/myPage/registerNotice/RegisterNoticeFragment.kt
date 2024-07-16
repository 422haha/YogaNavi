package com.ssafy.yoganavi.ui.homeUI.myPage.registerNotice

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ssafy.yoganavi.databinding.FragmentRegisterNoticeBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterNoticeFragment :
    BaseFragment<FragmentRegisterNoticeBinding>(FragmentRegisterNoticeBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)
            binding.ivPhoto.visibility = View.VISIBLE
            binding.ivCancel2.visibility = View.VISIBLE
        }
    }
}
