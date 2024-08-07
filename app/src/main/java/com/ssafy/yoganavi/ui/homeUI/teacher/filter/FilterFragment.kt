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
import com.ssafy.yoganavi.data.source.dto.teacher.FilterData
import com.ssafy.yoganavi.databinding.FragmentFilterBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.END
import com.ssafy.yoganavi.ui.utils.FILTER
import com.ssafy.yoganavi.ui.utils.ONE_TO_MULTI
import com.ssafy.yoganavi.ui.utils.ONE_TO_ONE
import com.ssafy.yoganavi.ui.utils.PERIOD_MONTH
import com.ssafy.yoganavi.ui.utils.PERIOD_THREE_MONTH
import com.ssafy.yoganavi.ui.utils.PERIOD_TOTAL
import com.ssafy.yoganavi.ui.utils.PERIOD_WEEK
import com.ssafy.yoganavi.ui.utils.RECENT
import com.ssafy.yoganavi.ui.utils.START
import com.ssafy.yoganavi.ui.utils.Week
import com.ssafy.yoganavi.ui.utils.formatZeroDate
import timber.log.Timber

class FilterFragment : BaseFragment<FragmentFilterBinding>(FragmentFilterBinding::inflate) {
    val viewModel: FilterViewModel by viewModels()
    private lateinit var weekToggleButtonMap: Map<Week, CheckBox>
    private val args by navArgs<FilterFragmentArgs>()
    private var isInit: Boolean = true
    private var sorting: Int = RECENT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, FILTER, true, "초기화") {
            binding.btnStartTime.text = "00:00"
            viewModel.filter.startTime = 0L
            binding.btnEndTime.text = "23:59"
            viewModel.filter.endTime = 86340000L
            binding.ibtnMon.isChecked = true
            binding.ibtnTue.isChecked = true
            binding.ibtnWed.isChecked = true
            binding.ibtnThu.isChecked = true
            binding.ibtnFri.isChecked = true
            binding.ibtnSat.isChecked = true
            binding.ibtnSun.isChecked = true
            binding.rbTotal.isChecked = true
            binding.rbOneToMulti.isChecked = true
            isInit = true
            val directions = FilterFragmentDirections
                .actionFilterFragmentToTeacherListFragment(viewModel.filter, isInit, sorting)
            findNavController().navigate(directions)
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
        isInit = args.isInit
        if (isInit) {
            viewModel.filter = FilterData()
        } else
            viewModel.filter = args.filter ?: FilterData()
        sorting = args.sorting
        binding.btnStartTime.text =
            (if (viewModel.filter.startTime > 0L) (viewModel.filter.startTime / 3600000).toInt()
                .toString() else "00") + ":" + if (viewModel.filter.startTime > 0L && ((viewModel.filter.startTime % 3600000) > 0L)) ((viewModel.filter.startTime % 3600000) / 60000).toInt()
                .toString() else "00"
        binding.btnEndTime.text =
            (if (viewModel.filter.endTime > 0L) ((viewModel.filter.endTime / 3600000).toInt()).toString() else "12") + ":" + if (viewModel.filter.endTime > 0L && ((viewModel.filter.endTime % 3600000) > 0L)) ((viewModel.filter.endTime % 3600000) / 60000).toInt()
                .toString() else "00"

        viewModel.filter.day.split(",").forEach {
            if (it.isNotBlank())
                viewModel.dayStatusMap[Week.valueOf(it.trim())] = true
        }
        weekToggleButtonMap.forEach { (day, toggleBtn) ->
            toggleBtn.isChecked = viewModel.dayStatusMap[day] ?: false
        }

        when (viewModel.filter.period) {
            PERIOD_WEEK -> binding.rbWeek.isChecked = true
            PERIOD_MONTH -> binding.rbMonth.isChecked = true
            PERIOD_THREE_MONTH -> binding.rbThreeMonth.isChecked = true
            PERIOD_TOTAL -> binding.rbTotal.isChecked = true
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
                ((materialTimePicker.hour * 3600) + (materialTimePicker.minute * 60)).toLong() * 1000

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

        weekToggleButtonMap.forEach { (day, toggleBtn) ->
            viewModel.dayStatusMap[day] = toggleBtn.isChecked
        }

        viewModel.filter.day =
            viewModel.dayStatusMap.filter { it.value }.keys.joinToString(",") // “MON,WED,SUN” 처럼 저장됨
        if (binding.rbWeek.isChecked) {
            viewModel.filter.period = PERIOD_WEEK
        } else if (binding.rbMonth.isChecked) {
            viewModel.filter.period = PERIOD_MONTH
        } else if (binding.rbThreeMonth.isChecked) {
            viewModel.filter.period = PERIOD_THREE_MONTH
        } else {
            viewModel.filter.period = PERIOD_TOTAL
        }

        if (binding.rbOneToOne.isChecked) {
            viewModel.filter.maxLiveNum = ONE_TO_ONE
        } else {
            viewModel.filter.maxLiveNum = ONE_TO_MULTI
        }
        isInit = false
        val directions = FilterFragmentDirections
            .actionFilterFragmentToTeacherListFragment(viewModel.filter, isInit, sorting)

        findNavController().navigate(directions)
    }
}