package com.ssafy.yoganavi.ui.homeUI.teacher.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentFilterBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.FILTER
import timber.log.Timber

class FilterFragment : BaseFragment<FragmentFilterBinding>(FragmentFilterBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, FILTER,true, "초기화") { Timber.d("초기화 구현 ㄱㄱ") }
//        showTimePicker()
    }

    private fun showTimePicker() {
        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTitleText("시작 시간")
            .setNegativeButtonText("취소")
            .setPositiveButtonText("확인")
            .setTheme(R.style.CustomMaterialTimePickerTheme)
            .build()

        materialTimePicker.addOnPositiveButtonClickListener {
            materialTimePicker.hour
            materialTimePicker.minute
        }

        materialTimePicker.show(childFragmentManager, "fragment_tag")
    }
}
