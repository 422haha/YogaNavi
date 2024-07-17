package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentTeacherDetailBinding
import com.ssafy.yoganavi.ui.core.BaseFragment

class TeacherDetailFragment :
    BaseFragment<FragmentTeacherDetailBinding>(FragmentTeacherDetailBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnReserve.setOnClickListener {
                findNavController().navigate(R.id.action_teacherDetailFragment_to_teacherReservationFragment)
            }
        }
    }
}
