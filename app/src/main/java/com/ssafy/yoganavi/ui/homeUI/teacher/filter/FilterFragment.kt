package com.ssafy.yoganavi.ui.homeUI.teacher.filter

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.teacher.FilterData
import com.ssafy.yoganavi.databinding.FragmentFilterBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.teacher.teacherList.TeacherListFragmentArgs
import com.ssafy.yoganavi.ui.utils.END
import com.ssafy.yoganavi.ui.utils.FILTER
import com.ssafy.yoganavi.ui.utils.START
import com.ssafy.yoganavi.ui.utils.Week
import com.ssafy.yoganavi.ui.utils.formatZeroDate
import timber.log.Timber

class FilterFragment : BaseFragment<FragmentFilterBinding>(FragmentFilterBinding::inflate) {
    val viewModel: FilterViewModel by viewModels()
    private lateinit var weekToggleButtonMap: Map<Week, CheckBox>
    private val args by navArgs<FilterFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, FILTER, true, "초기화") {
            binding.rbRecent.isChecked = true
            // TODO: 시간 설정
            binding.ibtnMon.isChecked = true
            binding.ibtnTue.isChecked = true
            binding.ibtnWed.isChecked = true
            binding.ibtnThu.isChecked = true
            binding.ibtnFri.isChecked = true
            binding.ibtnSat.isChecked = true
            binding.ibtnSun.isChecked = true
            binding.rbTotal.isChecked = true
            binding.rbOneToMulti.isChecked = true
        }
        // CheckBox마다 요일을 바인딩
        weekToggleButtonMap = mapOf(
            Week.MON to binding.ibtnMon,
            Week.TUE to binding.ibtnTue,
            Week.WED to binding.ibtnWed,
            Week.THU to binding.ibtnThu,
            Week.FRI to binding.ibtnFri,
            Week.SAT to binding.ibtnSat,
            Week.SUN to binding.ibtnSun
        )
        initView()

        initListener()
    }

    private fun initView() {
        viewModel.filter = args.filter ?: FilterData()
        if (viewModel.filter.sorting == 0) {
            binding.rbRecent.isChecked = true
            binding.rbPopular.isChecked = false
        } else {
            binding.rbPopular.isChecked = true
            binding.rbRecent.isChecked = false
        }

        binding.btnStartTime.text =
            (if (viewModel.filter.startTime > 0L) (viewModel.filter.startTime / 3600000).toInt()
                .toString() else "00") + ":" + if (viewModel.filter.startTime > 0L && ((viewModel.filter.startTime % 3600000) > 0L)) ((viewModel.filter.startTime % 3600000) / 60000).toInt()
                .toString() else "00"
        binding.btnEndTime.text =
            (if (viewModel.filter.endTime > 0L) ((viewModel.filter.endTime / 3600000).toInt()).toString() else "12") + ":" + if (viewModel.filter.endTime > 0L && ((viewModel.filter.endTime % 3600000) > 0L)) ((viewModel.filter.endTime % 3600000) / 60000).toInt()
                .toString() else "00"

        viewModel.filter.day.split(",").forEach {
            viewModel.dayStatusMap[Week.valueOf(it.trim())] = true
        }
        weekToggleButtonMap.forEach { (day, toggleBtn) ->
            toggleBtn.isChecked = viewModel.dayStatusMap[day] ?: false
        }

        when (viewModel.filter.period) {
            0 -> binding.rbWeek.isChecked = true
            1 -> binding.rbMonth.isChecked = true
            2 -> binding.rbThreeMonth.isChecked = true
            3 -> binding.rbTotal.isChecked = true
        }
        if (viewModel.filter.maxLiveNum == 0) {
            binding.rbOneToOne.isChecked = true
        } else {
            binding.rbOneToMulti.isChecked = true
        }
    }

    private fun showTimePicker(state: Int) {
        val title: String = if (state == START) "시작" else "종료"

        val prevTime: Long = if (state == START) viewModel.filter.startTime
        else if (state == END) viewModel.filter.endTime
        else 0

        val prevHour: Int = if (prevTime > 0L) (prevTime / 3600000).toInt()
        else 0
        val prevMinute: Int =
            if (prevTime > 0L && ((prevTime % 3600000) > 0L)) ((prevTime % 3600000) / 60000).toInt()
            else 0
        Timber.d("싸피 ${prevHour} : ${prevMinute} 원래는 ${prevTime}")

        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTitleText("$title 시간")
            .setNegativeButtonText("취소")
            .setPositiveButtonText("확인")
            .setHour(prevHour)
            .setMinute(prevMinute)
            .setTheme(R.style.CustomMaterialTimePickerTheme)
            .build()

        materialTimePicker.addOnPositiveButtonClickListener {
            val timeStr: String = formatZeroDate(materialTimePicker.hour, materialTimePicker.minute)
            val pickTime: Long =
                ((materialTimePicker.hour * 3600) + (materialTimePicker.minute * 60)).toLong()*1000

            if (state == START) {
                binding.btnStartTime.text = timeStr
                viewModel.filter.startTime = pickTime
            } else if (state == END) {
                binding.btnEndTime.text = timeStr
                viewModel.filter.endTime = pickTime
            }
        }

        materialTimePicker.show(childFragmentManager, "fragment_tag")
    }

    private fun initListener() {
        binding.btnFilterSave.setOnClickListener {
            if (!weekToggleButtonMap.values.any { it.isChecked }) {
                showSnackBar("요일을 선택하세요!")
            } else {
                goBackStack()
            }
        }
        binding.btnStartTime.setOnClickListener {
            showTimePicker(START)
        }
        binding.btnEndTime.setOnClickListener {
            showTimePicker(END)
        }
    }

    private fun goBackStack() {
        if (binding.rbRecent.isChecked) {
            viewModel.filter.sorting = 0
        } else {
            viewModel.filter.sorting = 1
        }

        weekToggleButtonMap.forEach { (day, toggleBtn) ->
            viewModel.dayStatusMap[day] = toggleBtn.isChecked
        }

        viewModel.filter.day =
            viewModel.dayStatusMap.filter { it.value }.keys.joinToString(",") // “MON,WED,SUN” 처럼 저장됨
        if (binding.rbWeek.isChecked) {
            viewModel.filter.period = 0
        } else if (binding.rbMonth.isChecked) {
            viewModel.filter.period = 1
        } else if (binding.rbThreeMonth.isChecked) {
            viewModel.filter.period = 2
        } else {
            viewModel.filter.period = 3
        }

        if (binding.rbOneToOne.isChecked) {
            viewModel.filter.maxLiveNum = 0
        } else {
            viewModel.filter.maxLiveNum = 1
        }
        val directions = FilterFragmentDirections
            .actionFilterFragmentToTeacherListFragment(viewModel.filter)

        findNavController().navigate(directions)
    }
}