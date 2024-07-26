package com.ssafy.yoganavi.ui.homeUI.myPage.registerLive

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
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
import com.ssafy.yoganavi.ui.utils.IntToDate
import com.ssafy.yoganavi.ui.utils.LIMIT_DATE
import com.ssafy.yoganavi.ui.utils.LIMIT_STR
import com.ssafy.yoganavi.ui.utils.MODIFY_LIVE
import com.ssafy.yoganavi.ui.utils.REGISTER
import com.ssafy.yoganavi.ui.utils.REGISTER_LIVE
import com.ssafy.yoganavi.ui.utils.START
import com.ssafy.yoganavi.ui.utils.UPDATE
import com.ssafy.yoganavi.ui.utils.Week
import com.ssafy.yoganavi.ui.utils.formatDotDate
import com.ssafy.yoganavi.ui.utils.formatTime
import com.ssafy.yoganavi.ui.utils.formatZeroDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar

@AndroidEntryPoint
class RegisterLiveFragment :
    BaseFragment<FragmentRegisterLiveBinding>(FragmentRegisterLiveBinding::inflate) {

    private val args: RegisterLiveFragmentArgs by navArgs()

    private val viewModel: RegisterLiveViewModel by viewModels()

    private lateinit var startDatePickerDialog: DatePickerDialog
    private lateinit var endDatePickerDialog: DatePickerDialog

    private lateinit var weekToggleButtonMap: Map<Week, ToggleButton>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()

        if (args.state == UPDATE)
            setModifyInfo()

        setToolbar(isBottomNavigationVisible = false,
            title = if (args.liveId == -1) REGISTER_LIVE else MODIFY_LIVE,
            canGoBack = true,
            menuItem = REGISTER,
            menuListener = { setRegister() })
    }

    private fun initListener() {
        with(binding) {
            tieStart.setOnClickListener { showCalendar(START) }

            tieEnd.setOnClickListener { showCalendar(END) }

            btnStart.setOnClickListener { showTimePicker(START) }

            btnEnd.setOnClickListener { showTimePicker(END) }

            weekToggleButtonMap = mapOf(
                Week.MON to binding.tbMon,
                Week.TUE to binding.tbThu,
                Week.WED to binding.tbWed,
                Week.THU to binding.tbThu,
                Week.FRI to binding.tbFri,
                Week.SAT to binding.tbSat,
                Week.SUN to binding.tbSun
            )

            cbEndDateUnlimited.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    tieEnd.setText(LIMIT_STR)
                    tieEnd.isEnabled = false
                    viewModel.liveLectureData.endDate = LIMIT_DATE
                } else {
                    tieEnd.setText(END_STR)
                    tieEnd.isEnabled = true
                    viewModel.liveLectureData.endDate = 0L
                }
            }
        }
    }

    private fun setModifyInfo() {
        viewModel.getLive(args.liveId) {
            lifecycleScope.launch {
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

                    val size = resources.getStringArray(R.array.maxnum_array).size
                    if (viewModel.liveLectureData.maxLiveNum in 1..size)
                        binding.spMaxNum.setSelection(viewModel.liveLectureData.maxLiveNum - 1)
                }
            }
        }
    }

    private fun setRegister() {
        if(!binding.etTitle.text.isNullOrBlank() &&
            !binding.etContent.text.isNullOrBlank() &&
            !weekToggleButtonMap.values.all { !it.isChecked } &&
            viewModel.liveLectureData.startDate != 0L &&
            viewModel.liveLectureData.endDate != 0L &&
            viewModel.liveLectureData.endTime != 0L) {

            viewModel.liveLectureData.apply {
                with(binding) {
                    liveTitle = etTitle.text.toString()
                    liveContent = etContent.text.toString()

                    weekToggleButtonMap.forEach { (day, toggleBtn) ->
                        viewModel.dayStatusMap[day] = toggleBtn.isSelected
                    }

                    viewModel.liveLectureData.availableDay =
                        viewModel.dayStatusMap
                            .filter { it.value }
                            .keys
                            .joinToString(separator = ",")

                    Timber.d("로오오그닷${viewModel.liveLectureData.availableDay}")

                    maxLiveNum = spMaxNum.selectedItemPosition + 1
                }
            }

            if (args.state == CREATE)
                viewModel.createLive(::popBackStack)
            else
                viewModel.updateLive(::popBackStack)
        } else { showSnackBar(IS_BLANK) }
    }

    private fun showCalendar(state: Int) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        if (state == START) {
            startDatePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.MySpinnerDatePickerStyle,
                { _, sYear, sMonth, sDay ->
                    calendar.set(sYear, sMonth, sDay)

                    with(viewModel.liveLectureData) {
                        binding.tieStart.setText(IntToDate(sYear, sMonth, sDay))
                        startDate = calendar.timeInMillis

                        if (endDate < startDate && endDate != 0L) {
                            binding.tieEnd.setText(IntToDate(sYear, sMonth, sDay))
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
                    calendar.set(sYear, sMonth, sDay)

                    binding.tieEnd.setText(IntToDate(sYear, sMonth, sDay))
                    viewModel.liveLectureData.endDate = calendar.timeInMillis
                }, year, month, day
            ).apply {
                datePicker.minDate = viewModel.liveLectureData.startDate
                show()
            }
        }
    }

    private fun showTimePicker(state: Int) {
        val title: String = if (state == START) "시작" else "종료"

        val prevTime: Long = if(state == START) viewModel.liveLectureData.startTime
        else if(state == END) viewModel.liveLectureData.endTime
        else 0

        val prevHour: Int = if (prevTime > 0L) (prevTime / 3600).toInt()
        else 0

        val prevMinute: Int = if (prevTime > 0L && ((prevTime % 3600) > 0L)) ((prevTime % 3600) / 60).toInt()
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
                ((materialTimePicker.hour * 3600) + (materialTimePicker.minute * 60)).toLong()

            if(state == START) {
                binding.btnStart.text = timeStr
                viewModel.liveLectureData.startTime = pickTime
            } else if(state == END) {
                binding.btnEnd.text = timeStr
                viewModel.liveLectureData.endTime = pickTime
            }
        }

        materialTimePicker.show(childFragmentManager, "fragment_tag")
    }

    private suspend fun popBackStack() = withContext(Dispatchers.Main){
        findNavController().popBackStack()
    }
}
