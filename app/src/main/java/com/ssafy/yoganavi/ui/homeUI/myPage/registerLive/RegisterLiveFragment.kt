package com.ssafy.yoganavi.ui.homeUI.myPage.registerLive

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentRegisterLiveBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.CREATE
import com.ssafy.yoganavi.ui.utils.END
import com.ssafy.yoganavi.ui.utils.END_STR
import com.ssafy.yoganavi.ui.utils.IS_BLANK
import com.ssafy.yoganavi.ui.utils.MODIFY_LIVE
import com.ssafy.yoganavi.ui.utils.REGISTER
import com.ssafy.yoganavi.ui.utils.REGISTER_LIVE
import com.ssafy.yoganavi.ui.utils.START
import com.ssafy.yoganavi.ui.utils.UPDATE
import com.ssafy.yoganavi.ui.utils.Week
import com.ssafy.yoganavi.ui.utils.addYear
import com.ssafy.yoganavi.ui.utils.formatDotDate
import com.ssafy.yoganavi.ui.utils.formatTime
import com.ssafy.yoganavi.ui.utils.formatZeroDate
import com.ssafy.yoganavi.ui.utils.intToDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class RegisterLiveFragment :
    BaseFragment<FragmentRegisterLiveBinding>(FragmentRegisterLiveBinding::inflate) {

    private val args: RegisterLiveFragmentArgs by navArgs()

    private val viewModel: RegisterLiveViewModel by viewModels()

    private lateinit var startDatePickerDialog: DatePickerDialog
    private lateinit var endDatePickerDialog: DatePickerDialog

    private lateinit var weekToggleButtonMap: Map<Week, CheckBox>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener(args.state)

        if (args.state == UPDATE)
            setModifyInfo()
    }

    override fun onStart() {
        super.onStart()
        setToolbar(
            isBottomNavigationVisible = false,
            title = if (args.liveId == -1) REGISTER_LIVE else MODIFY_LIVE,
            canGoBack = true,
            menuItem = REGISTER,
            menuListener = { setRegister() }
        )
    }

    private fun initListener(state: String) {
        with(binding) {
            tieStart.setOnClickListener { showCalendar(START, state) }

            tieEnd.setOnClickListener { showCalendar(END, state) }

            btnStart.setOnClickListener { showTimePicker(START) }

            btnEnd.setOnClickListener { showTimePicker(END) }

            weekToggleButtonMap = mapOf(
                Week.MON to binding.cbMon,
                Week.TUE to binding.cbTue,
                Week.WED to binding.cbWed,
                Week.THU to binding.cbThu,
                Week.FRI to binding.cbFri,
                Week.SAT to binding.cbSat,
                Week.SUN to binding.cbSun
            )

            cbEndDateUnlimited.setOnCheckedChangeListener { _, isChecked ->
                if (viewModel.liveLectureData.startDate != 0L) {
                    if (isChecked) {
                        tieEnd.isEnabled = false
                        viewModel.liveLectureData.endDate =
                            addYear(viewModel.liveLectureData.startDate)
                        tieEnd.setText(formatDotDate(viewModel.liveLectureData.endDate))
                    } else {
                        tieEnd.isEnabled = true
                        viewModel.liveLectureData.endDate = 0L
                        tieEnd.setText(END_STR)
                    }
                } else {
                    cbEndDateUnlimited.isChecked = false
                    showSnackBar("시작 날짜를 설정해주세요.")
                }
            }
        }
    }

    private fun setModifyInfo() {
        viewModel.getLive(args.liveId, ::onReadLive, ::setModifyUI)
    }

    private suspend fun setModifyUI() = withContext(Dispatchers.Main) {
        if (viewModel.liveLectureData.endDate < System.currentTimeMillis()) {//끝난강의
            binding.tieEnd.isEnabled = false
        }
        binding.cbMon.isEnabled = false
        binding.cbTue.isEnabled = false
        binding.cbWed.isEnabled = false
        binding.cbThu.isEnabled = false
        binding.cbFri.isEnabled = false
        binding.cbSat.isEnabled = false
        binding.cbSun.isEnabled = false
        binding.tieStart.isEnabled = false
        binding.cbEndDateUnlimited.isEnabled = false
        binding.btnStart.isEnabled = false
        binding.btnEnd.isEnabled = false
    }

    private fun setRegister() {
        hideKeyboard()

        if (!binding.etTitle.text.isNullOrBlank() &&
            !binding.etContent.text.isNullOrBlank() &&
            !weekToggleButtonMap.values.all { !it.isChecked } &&
            viewModel.liveLectureData.startDate != 0L &&
            viewModel.liveLectureData.endDate != 0L &&
            viewModel.liveLectureData.endTime != 0L
        ) {

            viewModel.liveLectureData.apply {
                with(binding) {
                    liveTitle = etTitle.text.toString()
                    liveContent = etContent.text.toString()

                    weekToggleButtonMap.forEach { (day, toggleBtn) ->
                        viewModel.dayStatusMap[day] = toggleBtn.isChecked
                    }

                    availableDay = viewModel.dayStatusMap.filter { it.value }.keys.joinToString(",")
                }
            }

            if (args.state == CREATE) viewModel.createLive(::popBackStack)
            else viewModel.updateLive(::popBackStack)

        } else {
            showSnackBar(IS_BLANK)
        }
    }

    private fun showCalendar(state: Int, isUpdate: String) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.MILLISECOND, 0)

        if (state == START) {
            startDatePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.MySpinnerDatePickerStyle,
                { _, sYear, sMonth, sDay ->
                    calendar.set(sYear, sMonth, sDay, 0, 0, 0)

                    with(viewModel.liveLectureData) {
                        binding.tieStart.setText(intToDate(sYear, sMonth, sDay))
                        startDate = calendar.timeInMillis

                        if (endDate < startDate && endDate != 0L) {
                            binding.tieEnd.setText(intToDate(sYear, sMonth, sDay))
                            endDate = calendar.timeInMillis
                        }
                    }
                }, year, month, day
            ).apply { show() }
        } else if (state == END) {
            endDatePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.MySpinnerDatePickerStyle,
                { _, sYear, sMonth, sDay ->
                    calendar.set(sYear, sMonth, sDay, 0, 0, 0)

                    if (calendar.timeInMillis.checkSameDate(viewModel.liveLectureData.startDate)) {
                        showSnackBar("최소 이틀 이상 선택해주세요!")
                        return@DatePickerDialog
                    }

                    binding.tieEnd.setText(intToDate(sYear, sMonth, sDay))
                    viewModel.liveLectureData.endDate = calendar.timeInMillis
                }, year, month, day
            ).apply {
                if (isUpdate == UPDATE) {
                    datePicker.minDate = viewModel.liveLectureData.endDate
                } else {
                    datePicker.minDate = viewModel.liveLectureData.startDate
                }
                show()
            }
        }
    }

    private fun Long.checkSameDate(time: Long): Boolean {
        val millisecondsPerDay = 24 * 60 * 60 * 1000L
        val startDate = (this / millisecondsPerDay) * millisecondsPerDay
        val endDate = (time / millisecondsPerDay) * millisecondsPerDay
        return startDate == endDate
    }

    private fun showTimePicker(state: Int) {
        val title: String = if (state == START) "시작" else "종료"

        val prevTime: Long = if (state == START) viewModel.liveLectureData.startTime
        else if (state == END) viewModel.liveLectureData.endTime
        else 0

        val prevHour: Int = if (prevTime > 0L) (prevTime / (3600 * 1000)).toInt()
        else 0

        val prevMinute: Int =
            if (prevTime > 0L && ((prevTime % (3600 * 1000)) > 0L)) ((prevTime % (3600 * 1000)) / (60 * 1000)).toInt()
            else 0

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
                ((materialTimePicker.hour * 3600 * 1000) + (materialTimePicker.minute * 60 * 1000)).toLong()

            if (state == START) {
                setDefaultEndTime(pickTime)
                binding.btnStart.text = timeStr
                viewModel.liveLectureData.startTime = pickTime
            } else if (state == END) {
                binding.btnEnd.text = timeStr
                viewModel.liveLectureData.endTime = pickTime
            }
        }

        materialTimePicker.show(childFragmentManager, "fragment_tag")
    }

    private fun setDefaultEndTime(startTime: Long) = with(binding) {
        val oneHourInMillis: Long = 60 * 60 * 1000L
        val endTime = startTime + oneHourInMillis
        val endHour = (endTime / (3600 * 1000)).toInt()
        val endMinute = (endTime / (60 * 1000) % 60).toInt()
        btnEnd.text = formatZeroDate(endHour, endMinute)
        viewModel.liveLectureData.endTime = endTime
    }

    private fun onReadLive() = lifecycleScope.launch {
        withContext(Dispatchers.Main) {
            binding.etTitle.setText(viewModel.liveLectureData.liveTitle)
            binding.etContent.setText(viewModel.liveLectureData.liveContent)

            viewModel.dayStatusMap.forEach { (day, isSelected) ->
                weekToggleButtonMap[day]?.isChecked = isSelected
            }

            binding.tieStart.setText(formatDotDate(viewModel.liveLectureData.startDate))
            binding.tieEnd.setText(formatDotDate(viewModel.liveLectureData.endDate))

            binding.btnStart.text = formatTime(viewModel.liveLectureData.startTime)
            binding.btnEnd.text = formatTime(viewModel.liveLectureData.endTime)

        }
    }

    private suspend fun popBackStack() = withContext(Dispatchers.Main) {
        findNavController().popBackStack()
    }
}