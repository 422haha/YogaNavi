package com.ssafy.yoganavi.ui.homeUI.teacher.filter

import android.os.Bundle
import android.view.View
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ssafy.yoganavi.databinding.FragmentFilterBinding
import com.ssafy.yoganavi.ui.core.BaseFragment

class FilterFragment : BaseFragment<FragmentFilterBinding>(FragmentFilterBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun showTimePicker() {
        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTitleText("시작 시간")
            .setNegativeButtonText("취소")
            .setPositiveButtonText("확인")
            .setTheme(android.R.style.Theme_Black)
            .build()

        materialTimePicker.addOnPositiveButtonClickListener {
            materialTimePicker.hour
            materialTimePicker.minute
        }

        materialTimePicker.show(requireActivity().supportFragmentManager, "fragment_tag")
    }
}
